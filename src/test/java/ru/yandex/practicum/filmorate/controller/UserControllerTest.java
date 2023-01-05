package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exeption.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void before() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    public void validationBadDateUser() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Throwable exception = assertThrows(ValidationException.class, () -> userController.validate(
                new User("email@mail", "login", LocalDate.now().plusDays(1).format(formatter))));
        assertEquals("введенная дата дня рождения еще не наступила", exception.getMessage());
    }

    @Test
    public void validationBlankNameUser() throws ValidationException {
        User testUser = new User("email@mail", "login", "2000-12-12");
        userController.validate(testUser);
        assertEquals(testUser.getName(), testUser.getLogin());
    }
}