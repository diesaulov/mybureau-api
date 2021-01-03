package de.mybureau.time.service.timer;

import java.time.LocalDateTime;

import static de.mybureau.time.utils.DateTimeUtils.nowInUtc;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;

public class ManualEntryRequest {
    private final long taskId;
    private final int durationInMinutes;
    private final LocalDateTime startedInUtc;
    private final String notes;

    private ManualEntryRequest(long taskId, int durationInMinutes, LocalDateTime startedInUtc, String notes) {
        isTrue(taskId > 0);
        isTrue(durationInMinutes > 0, "Duration has to be > 0");
        isTrue(durationInMinutes <= 480, "Maximum 480 minutes of duration allowed!");
        if (startedInUtc != null && startedInUtc.isAfter(nowInUtc())) {
            throw new IllegalArgumentException("The date has to be today or in the past!");
        }
        this.taskId = taskId;
        this.durationInMinutes = durationInMinutes;
        this.startedInUtc = startedInUtc;
        this.notes = notBlank(notes, "Notes cannot be blank!");
    }

    public long getTaskId() {
        return taskId;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public LocalDateTime getStartedInUtc() {
        return startedInUtc;
    }

    public String getNotes() {
        return notes;
    }

    public static ManualTimerRequestBuilder builder() {
        return new ManualTimerRequestBuilder();
    }


    public static final class ManualTimerRequestBuilder {
        private long taskId;
        private int durationInMinutes;
        private LocalDateTime startedInUtc;
        private String notes;

        private ManualTimerRequestBuilder() {
        }

        public ManualTimerRequestBuilder taskId(long taskId) {
            this.taskId = taskId;
            return this;
        }

        public ManualTimerRequestBuilder durationInMinutes(int durationInMinutes) {
            this.durationInMinutes = durationInMinutes;
            return this;
        }

        public ManualTimerRequestBuilder startedInUtc(LocalDateTime startedInUtc) {
            this.startedInUtc = startedInUtc;
            return this;
        }

        public ManualTimerRequestBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public ManualEntryRequest build() {
            return new ManualEntryRequest(taskId, durationInMinutes, startedInUtc, notes);
        }
    }
}
