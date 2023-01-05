package ru.yandex.practicum.filmorate.model.exeption;

public class ValidationException extends Exception {
    public ValidationException(final String message) {
        super(message);
    }
}
