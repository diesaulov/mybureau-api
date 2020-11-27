package de.bureau.time.service.timer.exception;

import de.bureau.time.service.timer.TimerErrors;

public class TimerInvalidStateException extends TimerException {

    private TimerInvalidStateException(String message) {
        super(TimerErrors.TIMER_ALREADY_STOPPED, message);
    }

    public static TimerInvalidStateException alreadyStopped(long timerId) {
        return new TimerInvalidStateException(String.format("Timer with id '%s' has been already stopped!", timerId));
    }

    public static TimerInvalidStateException stillRunning(long timerId) {
        return new TimerInvalidStateException(String.format("Timer with id '%s' is still running!", timerId));
    }

    public static TimerInvalidStateException noStoppedTimersToday() {
        return new TimerInvalidStateException("There is no stopped timer from today!");
    }

    public static TimerInvalidStateException hasRunningTimersToday() {
        return new TimerInvalidStateException("There are running timers from today!");
    }

    public static TimerInvalidStateException noTodayRunningTimers() {
        return new TimerInvalidStateException("There is no running timers from today!");
    }

    public static TimerInvalidStateException multipleRunningTimersDetected() {
        return new TimerInvalidStateException("Multiple running timers detected from today!");
    }
}
