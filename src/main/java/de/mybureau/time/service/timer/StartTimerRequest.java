package de.mybureau.time.service.timer;

import org.apache.commons.lang3.Validate;

import static org.apache.commons.lang3.Validate.isTrue;

public class StartTimerRequest {
    private final long taskId;
    private final String notes;
    private final long offsetInMinutes;

    private StartTimerRequest(long taskId, String notes, long offsetInMinutes) {
        isTrue(taskId > 0, "Task ID must be > 0");
        isTrue(offsetInMinutes >= 0, "Offset cannot be negative!");
        isTrue(offsetInMinutes <= 120, "Offset is way out of bounds! Max allowed value is 120");
        this.taskId = taskId;
        this.notes = Validate.notBlank(notes, "Notes cannot be empty! Be thorough!");
        this.offsetInMinutes = offsetInMinutes;
    }

    public long getTaskId() {
        return taskId;
    }

    public String getNotes() {
        return notes;
    }

    public long getOffsetInMinutes() {
        return offsetInMinutes;
    }

    public static StartTimerRequestBuilder builder() {
        return new StartTimerRequestBuilder();
    }

    public static final class StartTimerRequestBuilder {
        private long taskId;
        private String notes;
        private long offsetInMinutes;

        private StartTimerRequestBuilder() {
        }

        public StartTimerRequestBuilder taskId(long taskId) {
            this.taskId = taskId;
            return this;
        }

        public StartTimerRequestBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public StartTimerRequestBuilder offsetInMinutes(long offsetInMinutes) {
            this.offsetInMinutes = offsetInMinutes;
            return this;
        }

        public StartTimerRequest build() {
            return new StartTimerRequest(taskId, notes, offsetInMinutes);
        }
    }
}
