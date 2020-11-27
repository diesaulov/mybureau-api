package de.bureau.time.security;

public class InvalidTokenException extends Exception {

    public InvalidTokenException(Throwable cause) {
        super(cause);
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
