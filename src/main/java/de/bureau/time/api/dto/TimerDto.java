package de.bureau.time.api.dto;

import de.bureau.time.model.TimerEntry;
import de.bureau.time.model.TimerEntryType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimerDto {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'hh:mm:ss");

    public long id;
    public TimerEntryType type;
    public ProjectTaskDto task;
    public String projectName;
    public long durationInMinutes;
    public boolean isRunning;
    public String notes;

    public static TimerDto from(TimerEntry timerEntry) {
        final var timerDto = new TimerDto();
        final var taskType = timerEntry.getTask();

        timerDto.id = timerEntry.getId();
        timerDto.task = ProjectTaskDto.from(taskType);
        timerDto.projectName = taskType.getProject().getName();
        timerDto.durationInMinutes = timerEntry.calculateDurationInMinutes();
        timerDto.isRunning = timerEntry.isRunning();
        timerDto.notes = timerEntry.getNotes();
        timerDto.type = timerEntry.getType();

        return timerDto;
    }

    private static String formatDateTime(LocalDateTime localDateTime) {
        return localDateTime == null ? null : DATE_TIME_FORMATTER.format(localDateTime);
    }
}
