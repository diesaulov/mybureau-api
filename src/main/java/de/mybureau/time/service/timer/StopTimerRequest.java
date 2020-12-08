package de.mybureau.time.service.timer;

import org.apache.commons.lang3.Validate;

public class StopTimerRequest {
    private final long timerId;

    private StopTimerRequest(long timerId) {
        Validate.isTrue(timerId > 0, "Timer ID should be positive value!");
        this.timerId = timerId;
    }

    public long getTimerId() {
        return timerId;
    }

    public static StopTimerRequestBuilder builder() {
        return new StopTimerRequestBuilder();
    }


    public static final class StopTimerRequestBuilder {
        private long timerId;

        private StopTimerRequestBuilder() {
        }

        public StopTimerRequestBuilder timerId(long timerId) {
            this.timerId = timerId;
            return this;
        }

        public StopTimerRequest build() {
            return new StopTimerRequest(timerId);
        }
    }
}
