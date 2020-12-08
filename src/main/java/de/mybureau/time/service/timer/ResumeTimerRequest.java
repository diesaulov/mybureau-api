package de.mybureau.time.service.timer;

import org.apache.commons.lang3.Validate;

public class ResumeTimerRequest {
    private final long referenceTimerId;

    private ResumeTimerRequest(long referenceTimerId) {
        Validate.isTrue(referenceTimerId > 0, "Timer ID should be positive value!");
        this.referenceTimerId = referenceTimerId;
    }

    public long getReferenceTimerId() {
        return referenceTimerId;
    }

    public static StopTimerRequestBuilder builder() {
        return new StopTimerRequestBuilder();
    }

    public static final class StopTimerRequestBuilder {
        private long referenceTimerId;

        private StopTimerRequestBuilder() {
        }

        public StopTimerRequestBuilder referenceTimerId(long referenceTimerId) {
            this.referenceTimerId = referenceTimerId;
            return this;
        }

        public ResumeTimerRequest build() {
            return new ResumeTimerRequest(referenceTimerId);
        }
    }
}
