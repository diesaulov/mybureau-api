package de.bureau.time.api;

import de.bureau.time.api.dto.*;
import de.bureau.time.service.timer.*;
import de.bureau.time.utils.DateTimeUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/timers")
public class TimerApi {

    private final TimerService timerService;

    public TimerApi(TimerService timerService) {
        this.timerService = timerService;
    }

    @GetMapping
    public List<TimerDto> list(@RequestParam(required = false) String date) {
        final var listReq = ListRequest
                .builder()
                .date(date == null ? DateTimeUtils.todayInUtc() : LocalDate.parse(date))
                .build();
        return timerService.list(listReq)
                .stream()
                .sorted(Comparator.comparing(te -> te.getTimerStarted() == null ? te.getDate().plusDays(1).atStartOfDay().plusSeconds(te.getId())
                        : te.getTimerStarted()))
                .map(TimerDto::from)
                .collect(Collectors.toList());
    }

    @PostMapping
    public TimerDto startTimer(@RequestBody StartTimerDto startTimerDto) {
        final var newTimer = timerService.startTimer(StartTimerRequest.builder()
                .taskTypeId(startTimerDto.taskTypeId)
                .notes(startTimerDto.notes)
                .offsetInMinutes(startTimerDto.offsetInMinutes)
                .build());

        return TimerDto.from(newTimer);
    }

    @PostMapping("manual")
    public TimerDto manual(@RequestBody ManualTimerDto manualTimerDto) {
        final var newTimer = timerService.manualEntry(ManualEntryRequest.builder()
                .taskTypeId(manualTimerDto.taskTypeId)
                .durationInMinutes(manualTimerDto.durationInMinutes)
                .notes(manualTimerDto.notes)
                .date(manualTimerDto.date)
                .build());

        return TimerDto.from(newTimer);
    }

    @PutMapping("{id}")
    public TimerDto updateTimer(@PathVariable("id") long timerId,
                                @RequestBody @Valid UpdateTimerDto updateTimerDto) {
        final var updatedTimer = timerService.updateTimer(UpdateTimerRequest.builder()
                .timerId(timerId)
                .notes(updateTimerDto.notes)
                .build());

        return TimerDto.from(updatedTimer);
    }

    @PostMapping("{id}/resume")
    public TimerDto resumeTimer(@PathVariable("id") long timerId) {
        final var newTimer = timerService.resumeTimer(ResumeTimerRequest.builder()
                .referenceTimerId(timerId)
                .build());

        return TimerDto.from(newTimer);
    }

    @PostMapping("resume")
    public void resumeLastTimer() {
        timerService.resumeLastStoppedTimer();
    }

    @PostMapping("{id}/stop")
    public void stopTimer(@PathVariable("id") long timerId) {
        timerService.stopTimer(StopTimerRequest.builder()
                .timerId(timerId)
                .build());
    }

    @PostMapping("stop")
    public void stopRunningTimer() {
        timerService.stopRunningTimer();
    }

    @DeleteMapping("{id}")
    public void deleteTimer(@PathVariable("id") long timerId) {
        timerService.deleteTimer(DeleteTimerRequest.builder()
                .timerId(timerId)
                .build());
    }
}