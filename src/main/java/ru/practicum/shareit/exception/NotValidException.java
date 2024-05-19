package ru.practicum.shareit.exception;

public class NotValidException extends RuntimeException {
    public NotValidException() {
    }

    public NotValidException(final String message) {
        super(message);
    }

    public NotValidException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NotValidException(final Throwable cause) {
        super(cause);
    }
}
