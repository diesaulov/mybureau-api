package de.mybureau.time.service.timer;

import de.mybureau.time.model.Project;
import de.mybureau.time.model.ProjectTask;
import de.mybureau.time.model.TimerEntry;

public class ReportEntry {

    private final String periodLabel;
    private final Project project;
    private final ProjectTask task;
    private final String notes;
    private long durationInSeconds;

    private ReportEntry(String periodLabel, Project project, ProjectTask task, String notes, long durationInSeconds) {
        this.periodLabel = periodLabel;
        this.project = project;
        this.task = task;
        this.notes = notes;
        this.durationInSeconds = durationInSeconds;
    }

    public static ReportEntry init(String periodLabel, TimerEntry timerEntry) {
        return new ReportEntry(periodLabel, timerEntry.getTask().getProject(),
                timerEntry.getTask(), timerEntry.getNotes(), timerEntry.calculateDurationInSecs());
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

    public long getDurationInSeconds() {
        return durationInSeconds;
    }

    public ReportEntry accumulate(long durationInMinutes) {
        this.durationInSeconds += durationInMinutes;
        return this;
    }
}
