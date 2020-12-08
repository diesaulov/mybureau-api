package de.mybureau.time.service.timer;

import static org.apache.commons.lang3.Validate.*;

public class UpdateTimerRequest {
    private final long timerId;
    private final String notes;

    private UpdateTimerRequest(long timerId, String notes) {
        isTrue(timerId > 0, "Timer ID has to be > 0");
        this.timerId = timerId;
        this.notes = notBlank(notes);
    }

    public long getTimerId() {
        return timerId;
    }

    public String getNotes() {
        return notes;
    }

    public static UpdateTimerRequestBuilder builder() {
        return new UpdateTimerRequestBuilder();
    }

    public static final class UpdateTimerRequestBuilder {
        private long timerId;
        private String notes;

        private UpdateTimerRequestBuilder() {
        }

        public UpdateTimerRequestBuilder timerId(long timerId) {
            this.timerId = timerId;
            return this;
        }

        public UpdateTimerRequestBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public UpdateTimerRequest build() {
            return new UpdateTimerRequest(timerId, notes);
        }
    }
}
