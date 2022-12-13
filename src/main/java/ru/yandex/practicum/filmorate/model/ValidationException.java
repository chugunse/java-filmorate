package ru.yandex.practicum.filmorate.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ValidationException extends Exception {
    public ValidationException(final String message) {
        super(message);
    }
}
