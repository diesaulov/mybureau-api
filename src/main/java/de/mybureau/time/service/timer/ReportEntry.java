package de.mybureau.time.service.timer;

import de.mybureau.time.model.Project;
import de.mybureau.time.model.ProjectTask;
import de.mybureau.time.model.TimerEntry;

public class ReportEntry {

    private final String periodLabel;
    private final Project project;
    private final ProjectTask task;
    private final String notes;
    private long durationInMinutes;

    private ReportEntry(String periodLabel, Project project, ProjectTask task, String notes, long durationInMinutes) {
        this.periodLabel = periodLabel;
        this.project = project;
        this.task = task;
        this.notes = notes;
        this.durationInMinutes = durationInMinutes;
    }

    public static ReportEntry init(String periodLabel, TimerEntry timerEntry) {
        return new ReportEntry(periodLabel, timerEntry.getTask().getProject(),
                timerEntry.getTask(), timerEntry.getNotes(), timerEntry.calculateDurationInMinutes());
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public Project getProject() {
        return project;
    }

    public ProjectTask getTask() {
        return task;
    }

    public String getNotes() {
        return notes;
    }

    public long getDurationInMinutes() {
        return durationInMinutes;
    }

    public ReportEntry accumulate(long durationInMinutes) {
        this.durationInMinutes += durationInMinutes;
        return this;
    }
}
