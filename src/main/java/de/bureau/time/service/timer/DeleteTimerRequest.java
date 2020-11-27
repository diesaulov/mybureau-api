package de.bureau.time.service.timer;

import org.apache.commons.lang3.Validate;

public class DeleteTimerRequest {
    private final long timerId;

    private DeleteTimerRequest(long timerId) {
        Validate.isTrue(timerId > 0, "Timer ID should be positive value!");
        this.timerId = timerId;
    }

    public long getTimerId() {
        return timerId;
    }

    public static DeleteTimerRequestBuilder builder() {
        return new DeleteTimerRequestBuilder();
    }

    public static final class DeleteTimerRequestBuilder {
        private long timerId;

        private DeleteTimerRequestBuilder() {
        }

        public DeleteTimerRequestBuilder timerId(long timerId) {
            this.timerId = timerId;
            return this;
        }

        public DeleteTimerRequest build() {
            return new DeleteTimerRequest(timerId);
        }
    }
}
