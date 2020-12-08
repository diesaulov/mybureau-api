package de.mybureau.time.service.timer;

import de.mybureau.time.model.Project;
import de.mybureau.time.model.ProjectTask;
import de.mybureau.time.model.TimerEntry;

public class ReportEntry {

    private final Project project;
    private final ProjectTask task;
    private final String notes;
    private long durationInMinutes;

    private ReportEntry(Project project, ProjectTask task, String notes, long durationInMinutes) {
        this.project = project;
        this.task = task;
        this.notes = notes;
        this.durationInMinutes = durationInMinutes;
    }

    public static ReportEntry init(TimerEntry timerEntry) {
        return new ReportEntry(timerEntry.getTask().getProject(),
                timerEntry.getTask(), timerEntry.getNotes(), timerEntry.calculateDurationInMinutes());
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
