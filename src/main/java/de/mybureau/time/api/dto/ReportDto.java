package de.mybureau.time.api.dto;

import de.mybureau.time.service.timer.ReportEntry;

public class ReportDto {
    public String projectName;
    public long projectId;
    public String taskName;
    public long taskId;
    public String notes;
    public long duration;

    public static ReportDto from(ReportEntry reportEntry) {
        final var dto = new ReportDto();
        dto.projectId = reportEntry.getProject().getId();
        dto.projectName = reportEntry.getProject().getName();
        dto.taskId = reportEntry.getTask().getId();
        dto.taskName = reportEntry.getTask().getName();
        dto.notes = reportEntry.getNotes();
        dto.duration = reportEntry.getDurationInMinutes();
        return dto;
    }
}
