package de.bureau.time.service.timer;

import de.bureau.time.model.TimerEntry;
import de.bureau.time.model.TimerEntryType;
import de.bureau.time.repository.TimeEntryRepository;
import de.bureau.time.service.project.ProjectService;
import de.bureau.time.service.timer.exception.TimerInvalidStateException;
import de.bureau.time.service.timer.exception.TimerNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.bureau.time.service.timer.ListRequest.forToday;
import static de.bureau.time.utils.DateTimeUtils.nowInUtc;

@Service
public class TimerService {

    private final ProjectService projectService;
    private final TimeEntryRepository timeEntryRepository;

    public TimerService(ProjectService projectService,
                        TimeEntryRepository timeEntryRepository) {
        this.projectService = projectService;
        this.timeEntryRepository = timeEntryRepository;
    }

    @Transactional
    public List<TimerEntry> list(ListRequest listRequest) {
        final var date = listRequest.getDate();
        return Stream.concat(
                timeEntryRepository.findByDeletedFalseAndTimerStartedIsBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay()),
                timeEntryRepository.findByDeletedFalseAndDate(date))
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    public TimerEntry startTimer(StartTimerRequest startTimerRequest) {
        final var taskType = projectService.task(startTimerRequest.getTaskTypeId());
        final var offset = startTimerRequest.getOffsetInMinutes();
        final var newTimerEntry = new TimerEntry();
        newTimerEntry.setInsertedTs(nowInUtc());
        newTimerEntry.setTimerStarted(offset > 0 ? nowInUtc().minusMinutes(offset) : nowInUtc());
        newTimerEntry.setTask(taskType);
        newTimerEntry.setType(TimerEntryType.TIMER);
        newTimerEntry.setNotes(startTimerRequest.getNotes());

        return timeEntryRepository.save(newTimerEntry);
    }

    @Transactional
    public TimerEntry manualEntry(ManualEntryRequest manualEntryRequest) {
        final var taskType = projectService.task(manualEntryRequest.getTaskTypeId());
        final var newTimerEntry = new TimerEntry();
        newTimerEntry.setInsertedTs(nowInUtc());
        newTimerEntry.setDate(manualEntryRequest.getDate());
        newTimerEntry.setTask(taskType);
        newTimerEntry.setType(TimerEntryType.MANUAL);
        newTimerEntry.setDurationInMinutes(manualEntryRequest.getDurationInMinutes());
        newTimerEntry.setNotes(manualEntryRequest.getNotes());
        return timeEntryRepository.save(newTimerEntry);
    }

    @Transactional
    public TimerEntry resumeTimer(ResumeTimerRequest resumeTimerRequest) {
        final var referenceTimer = findTimer(resumeTimerRequest.getReferenceTimerId());

        if (referenceTimer.isRunning()) {
            throw TimerInvalidStateException.stillRunning(referenceTimer.getId());
        }

        return startTimer(StartTimerRequest.builder()
                .taskTypeId(referenceTimer.getTask().getId())
                .notes(referenceTimer.getNotes())
                .build());
    }

    @Transactional
    public TimerEntry updateTimer(UpdateTimerRequest updateTimerRequest) {
        final var timer = findTimer(updateTimerRequest.getTimerId());
        timer.setNotes(updateTimerRequest.getNotes());
        timer.setUpdatedTs(nowInUtc());
        return timer;
    }

    @Transactional
    public void stopRunningTimer() {
        final var todayTimers = list(forToday()).stream()
                .filter(TimerEntry::isRunning)
                .collect(Collectors.toUnmodifiableList());

        if (todayTimers.size() == 0) {
            throw TimerInvalidStateException.noTodayRunningTimers();
        }

        if (todayTimers.size() > 1) {
            throw TimerInvalidStateException.multipleRunningTimersDetected();
        }

        stopTimer(StopTimerRequest.builder()
                .timerId(todayTimers.get(0).getId())
                .build());
    }

    @Transactional
    public void resumeLastStoppedTimer() {
        final var todayTimers = list(forToday()).stream()
                .filter(TimerEntry::isRunning)
                .collect(Collectors.toUnmodifiableList());

        if (todayTimers.size() > 0) {
            throw TimerInvalidStateException.hasRunningTimersToday();
        }

        final var lastStoppedFromToday = list(forToday()).stream()
                .filter(TimerEntry::isStopped)
                .filter(t -> t.getType() == TimerEntryType.TIMER)
                .sorted(Comparator.comparing(TimerEntry::getTimerStopped).reversed())
                .findFirst().orElseThrow(TimerInvalidStateException::noStoppedTimersToday);

        resumeTimer(ResumeTimerRequest.builder()
                .referenceTimerId(lastStoppedFromToday.getId())
                .build());
    }

    @Transactional
    public void stopTimer(StopTimerRequest stopTimerRequest) {
        final var timeEntry = findTimer(stopTimerRequest.getTimerId());

        if (timeEntry.getTimerStopped() != null) {
            throw TimerInvalidStateException.alreadyStopped(timeEntry.getId());
        }

        timeEntry.setTimerStopped(nowInUtc());
    }

    private TimerEntry findTimer(long id) {
        return timeEntryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> TimerNotFoundException.forId(id));
    }

    @Transactional
    public void deleteTimer(DeleteTimerRequest deleteTimerRequest) {
        final var timerEntry = findTimer(deleteTimerRequest.getTimerId());
        timerEntry.setDeleted(true);
        timerEntry.setDeletedTs(nowInUtc());
    }
}
