package de.mybureau.time.service.timer.exception;

import de.mybureau.time.service.DomainException;

public abstract class TimerException extends DomainException {

    protected TimerException(int errorCode, String message) {
        super(errorCode, message);
    }
}
