package ru.practicum.shareit.exception;

public class DuplicateException extends RuntimeException {

    public DuplicateException() {
    }

    public DuplicateException(final String message) {
        super(message);
    }

    public DuplicateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DuplicateException(final Throwable cause) {
        super(cause);
    }
}
