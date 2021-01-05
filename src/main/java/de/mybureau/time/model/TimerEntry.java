package de.mybureau.time.model;

import de.mybureau.time.utils.DateTimeUtils;

import javax.annotation.Nullable;
import javax.persistence.*;
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

    @Column(name = "duration_sec")
    private Long durationInSeconds;

    @Column(name = "started")
    private LocalDateTime started;

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

    public Long getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(Long durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    public void setStarted(LocalDateTime timerStarted) {
        this.started = timerStarted;
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
        return type == TimerEntryType.TIMER && durationInSeconds == null;
    }

    public boolean isStopped() {
        return !isRunning();
    }

    public long calculateDurationInSecs() {
        return durationInSeconds != null ? durationInSeconds : getStarted().until(DateTimeUtils.nowInUtc(), ChronoUnit.SECONDS);
    }

    @Nullable
    public LocalDateTime getStopped() {
        return isStopped() ? started.plusSeconds(durationInSeconds) : null;
    }
}
