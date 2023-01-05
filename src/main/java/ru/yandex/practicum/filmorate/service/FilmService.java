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

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) throws Exception {
        return filmStorage.create(film);
    }

    public Film update(Film film) throws Exception {
        return filmStorage.update(film);
    }

    public Film getById(int id) throws Exception {
        return filmStorage.getById(id);
    }

    public Film deleteById(int id) throws Exception {
        return filmStorage.deleteById(id);
    }

    public Film addLike(int filmId, int userId) {

        return filmStorage.addLike(filmId, userId);
    }

    public Film removeLike(int filmId, int userId) {
        return filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getBestFilms(int count) {
        return filmStorage.getBestFilms(count);
    }
}
