package ru.yandex.practicum.filmorate.model.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BeginOfCinemaValidator.class)
public @interface BeginOfCinema {
    String message() default "Введена неверная дата релиза (раньше появления первого фильма 1895-12-28)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}