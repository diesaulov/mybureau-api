package de.mybureau.time.service.timer;

import de.mybureau.time.model.TimerEntry;
import de.mybureau.time.model.TimerEntryType;
import de.mybureau.time.repository.TimerEntryRepository;
import de.mybureau.time.service.project.ProjectService;
import de.mybureau.time.service.timer.exception.TimerInvalidStateException;
import de.mybureau.time.service.timer.exception.TimerNotFoundException;
import de.mybureau.time.utils.DateTimeHelper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import static de.mybureau.time.utils.DateTimeUtils.endOfDay;
import static de.mybureau.time.utils.DateTimeUtils.startOfDay;

@Service
public class TimerService {

    private final ProjectService projectService;
    private final TimerEntryRepository timerEntryRepository;
    private final DateTimeHelper dateTimeHelper;

    public TimerService(ProjectService projectService,
                        TimerEntryRepository timerEntryRepository, DateTimeHelper dateTimeHelper) {
        this.projectService = projectService;
        this.timerEntryRepository = timerEntryRepository;
        this.dateTimeHelper = dateTimeHelper;
    }

    @Transactional
    public List<TimerEntry> list(ListRequest listRequest) {
        final var date = listRequest.getDate();
        return timerEntryRepository.findByDeletedFalseAndStartedIsBetween(startOfDay(date), endOfDay(date));
    }

    @Transactional
    public TimerEntry startTimer(StartTimerRequest startTimerRequest) {
        final var task = projectService.findTask(startTimerRequest.getTaskId());
        final var offset = startTimerRequest.getOffsetInMinutes();
        final var newTimerEntry = new TimerEntry();
        final var nowInUtc = dateTimeHelper.nowInUtc();

        newTimerEntry.setInsertedTs(nowInUtc);
        newTimerEntry.setStarted(offset > 0 ? nowInUtc.minusMinutes(offset) : nowInUtc);
        newTimerEntry.setTask(task);
        newTimerEntry.setType(TimerEntryType.TIMER);
        newTimerEntry.setNotes(startTimerRequest.getNotes());

        return timerEntryRepository.save(newTimerEntry);
    }

    @Transactional
    public TimerEntry manualEntry(ManualEntryRequest manualEntryRequest) {
        final var task = projectService.findTask(manualEntryRequest.getTaskId());
        final var newTimerEntry = new TimerEntry();

        newTimerEntry.setStarted(manualEntryRequest.getStartedInUtc() == null ? dateTimeHelper.nowInUtc() : manualEntryRequest.getStartedInUtc());
        newTimerEntry.setType(TimerEntryType.MANUAL);
        newTimerEntry.setTask(task);
        newTimerEntry.setDurationInSeconds(manualEntryRequest.getDurationInMinutes() * 60L);
        newTimerEntry.setNotes(manualEntryRequest.getNotes());
        newTimerEntry.setInsertedTs(dateTimeHelper.nowInUtc());

        return timerEntryRepository.save(newTimerEntry);
    }

    @Transactional
    public TimerEntry resumeTimer(ResumeTimerRequest resumeTimerRequest) {
        final var referenceTimer = findTimer(resumeTimerRequest.getReferenceTimerId());

        if (referenceTimer.isRunning()) {
            throw TimerInvalidStateException.stillRunning(referenceTimer.getId());
        }

        return startTimer(StartTimerRequest.builder()
                .taskId(referenceTimer.getTask().getId())
                .notes(referenceTimer.getNotes())
                .build());
    }

    @Transactional
    public TimerEntry updateTimer(UpdateTimerRequest updateTimerRequest) {
        final var timer = findTimer(updateTimerRequest.getTimerId());
        timer.setNotes(updateTimerRequest.getNotes());
        timer.setUpdatedTs(dateTimeHelper.nowInUtc());
        return timer;
    }

    @Transactional
    public void stopRunningTimer() {
        final var todayRunningTimers = todayRunningTimers();

        if (todayRunningTimers.size() == 0) {
            throw TimerInvalidStateException.noTodayRunningTimers();
        }

        if (todayRunningTimers.size() > 1) {
            throw TimerInvalidStateException.multipleRunningTimersDetected();
        }

        stopTimer(StopTimerRequest.builder()
                .timerId(todayRunningTimers.get(0).getId())
                .build());
    }

    private List<TimerEntry> todayRunningTimers() {
        final var todayInUtc = dateTimeHelper.todayInUtc();
        return timerEntryRepository.findRunningTimers(startOfDay(todayInUtc), endOfDay(todayInUtc));
    }

    @Transactional
    public void resumeLastStoppedTimer() {

        if (countTodayRunningTimers() > 0) {
            throw TimerInvalidStateException.hasRunningTimersToday();
        }

        final var lastStoppedFromToday = todayStoppedTimers()
                .stream().max(Comparator.comparing(TimerEntry::getStopped))
                .orElseThrow(TimerInvalidStateException::noStoppedTimersToday);

        resumeTimer(ResumeTimerRequest.builder()
                .referenceTimerId(lastStoppedFromToday.getId())
                .build());
    }

    private int countTodayRunningTimers() {
        final var todayInUtc = dateTimeHelper.todayInUtc();
        return timerEntryRepository.countRunningTimers(startOfDay(todayInUtc), endOfDay(todayInUtc));
    }

    private List<TimerEntry> todayStoppedTimers() {
        final var todayInUtc = dateTimeHelper.todayInUtc();
        return timerEntryRepository.findStoppedTimers(startOfDay(todayInUtc), endOfDay(todayInUtc));
    }

    @Transactional
    public void stopTimer(StopTimerRequest stopTimerRequest) {
        final var timeEntry = findTimer(stopTimerRequest.getTimerId());

        if (timeEntry.isStopped()) {
            throw TimerInvalidStateException.alreadyStopped(timeEntry.getId());
        }

        timeEntry.setDurationInSeconds(timeEntry.getStarted().until(dateTimeHelper.nowInUtc(), ChronoUnit.SECONDS));
        timeEntry.setUpdatedTs(dateTimeHelper.nowInUtc());
    }

    @Transactional
    public TimerEntry findTimer(long id) {
        return timerEntryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> TimerNotFoundException.forId(id));
    }

    @Transactional
    public void deleteTimer(DeleteTimerRequest deleteTimerRequest) {
        final var timerEntry = findTimer(deleteTimerRequest.getTimerId());
        timerEntry.setDeleted(true);
        timerEntry.setDeletedTs(dateTimeHelper.nowInUtc());
    }
}
