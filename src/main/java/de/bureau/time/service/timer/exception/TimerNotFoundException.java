package de.bureau.time.service.timer.exception;

import de.bureau.time.service.timer.TimerErrors;

public final class TimerNotFoundException extends TimerException {

    private TimerNotFoundException(String message) {
        super(TimerErrors.TIMER_NOT_FOUND, message);
    }

    public static TimerNotFoundException forId(long id) {
        return new TimerNotFoundException(String.format("Timer with '%s' not found!", id));
    }
}
