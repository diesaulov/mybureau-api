package de.bureau.time.service.timer;

import static org.apache.commons.lang3.Validate.isTrue;

public class StartTimerRequest {
    private final long taskTypeId;
    private final String notes;
    private final long offsetInMinutes;

    private StartTimerRequest(long taskTypeId, String notes, long offsetInMinutes) {
        isTrue(taskTypeId > 0, "Task type ID must be > 0");
        isTrue(offsetInMinutes >= 0, "Offset cannot be negative!");
        isTrue(offsetInMinutes <= 120, "Offset is way out of bounds! Max allowed value is 120");
        this.taskTypeId = taskTypeId;
        this.notes = notes;
        this.offsetInMinutes = offsetInMinutes;
    }

    public long getTaskTypeId() {
        return taskTypeId;
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
        private long taskTypeId;
        private String notes;
        private long offsetInMinutes;

        private StartTimerRequestBuilder() {
        }

        public StartTimerRequestBuilder taskTypeId(long taskTypeId) {
            this.taskTypeId = taskTypeId;
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
            return new StartTimerRequest(taskTypeId, notes, offsetInMinutes);
        }
    }
}
