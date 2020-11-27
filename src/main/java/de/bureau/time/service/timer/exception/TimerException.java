package de.bureau.time.service.timer.exception;

import de.bureau.time.service.DomainException;

public abstract class TimerException extends DomainException {

    protected TimerException(int errorCode, String message) {
        super(errorCode, message);
    }
}
