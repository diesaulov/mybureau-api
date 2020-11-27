package de.bureau.time.model;

import de.bureau.time.utils.DateTimeUtils;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "timer_entry")
public class TimerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TimerEntryType type;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "duration")
    private int durationInMinutes;

    @Column(name = "timer_started")
    private LocalDateTime timerStarted;

    @Column(name = "timer_stopped")
    private LocalDateTime timerStopped;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private ProjectTask task;

    @Column(name = "notes")
    private String notes;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "inserted_ts")
    private LocalDateTime insertedTs;

    @Column(name = "updated_ts")
    private LocalDateTime updatedTs;

    @Column(name = "deleted_ts")
    private LocalDateTime deletedTs;

    public TimerEntry() {
    }

    public long getId() {
        return id;
    }

    public TimerEntryType getType() {
        return type;
    }

    public void setType(TimerEntryType type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public LocalDateTime getTimerStarted() {
        return timerStarted;
    }

    public void setTimerStarted(LocalDateTime timerStarted) {
        this.timerStarted = timerStarted;
    }

    public LocalDateTime getTimerStopped() {
        return timerStopped;
    }

    public void setTimerStopped(LocalDateTime timerStopped) {
        this.timerStopped = timerStopped;
    }

    public ProjectTask getTask() {
        return task;
    }

    public void setTask(ProjectTask taskType) {
        this.task = taskType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getDeletedTs() {
        return deletedTs;
    }

    public void setDeletedTs(LocalDateTime deletedTs) {
        this.deletedTs = deletedTs;
    }

    public LocalDateTime getInsertedTs() {
        return insertedTs;
    }

    public void setInsertedTs(LocalDateTime insertedTs) {
        this.insertedTs = insertedTs;
    }

    public LocalDateTime getUpdatedTs() {
        return updatedTs;
    }

    public void setUpdatedTs(LocalDateTime updateTs) {
        this.updatedTs = updateTs;
    }

    public boolean isRunning() {
        return timerStopped == null && type == TimerEntryType.TIMER;
    }

    public boolean isStopped() {
        return !isRunning();
    }

    public long calculateDurationInMinutes() {
        if (getType() == TimerEntryType.TIMER) {
            if (getTimerStopped() == null) {
                return getTimerStarted().until(DateTimeUtils.nowInUtc(), ChronoUnit.MINUTES);
            } else {
                return getTimerStarted().until(getTimerStopped(), ChronoUnit.MINUTES);
            }
        } else {
            return getDurationInMinutes();
        }
    }
}
