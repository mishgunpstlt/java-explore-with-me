package ru.practicum.exception;

public class NotMetConditionsException extends RuntimeException {
    public NotMetConditionsException(String message) {
        super(message);
    }
}
