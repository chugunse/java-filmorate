package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        checkFilm(film.getId());
        return filmStorage.update(film);
    }

    public Film getById(int id) {
        checkFilm(id);
        return filmStorage.getById(id);
    }

    public Film deleteById(int id) {
        checkFilm(id);
        return filmStorage.deleteById(id);
    }

    public Film addLike(int filmId, int userId) {
        checkFilm(filmId);
        userService.checkUser(userId);
        return filmStorage.addLike(filmId, userId);
    }

    public Film removeLike(int filmId, int userId) {
        checkFilm(filmId);
        userService.checkUser(userId);
        return filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getBestFilms(int count) {
        return filmStorage.getBestFilms(count);
    }

    private void checkFilm(int id) {
        filmStorage.checkFilm(id);
    }
}
