package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exeption.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.exeption.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id = 0;
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate MINDATA = LocalDate.of(1895, 12, 28);

    private int genId() {
        return ++id;
    }

    @Override
    public List<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) throws Exception {
        log.info("Запрос POST /films " + film);
        if (validate(film) && !films.containsKey(film.getId())) {
            film.setId(genId());
            films.put(film.getId(), film);
        }
        return film;
    }

    @Override
    public Film update(Film film) throws Exception {
        log.info("Запрос PUT /films " + film);
        validate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new ObjectNotFoundException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    @Override
    public Film getById(int id) {
        log.info("Запрос Get /films " + id);
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new ObjectNotFoundException("Фильм с id " + id + " не найден");
        }
    }

    @Override
    public Film deleteById(int id) {
        log.info("Запрос Del /films " + id);
        if (films.containsKey(id)) {
            Film film = films.get(id);
            films.remove(id);
            return film;
        } else {
            throw new ObjectNotFoundException("Фильм с id " + id + " не найден");
        }
    }

    @Override
    public Film addLike(int filmId, int userId) {
        log.info("Запрос PUT /films/{id}/like/{userId} " + filmId + " " + userId);
        if (!films.containsKey(filmId)) {
            throw new ObjectNotFoundException("Фильм с id " + id + " не найден");
        } else {
            films.get(filmId).getLikes().add(userId);
            return films.get(filmId);
        }
    }

    @Override
    public Film removeLike(int filmId, int userId) {
        log.info("Запрос Del /films/{id}/like/{userId}" + filmId + " " + userId);
        if (!films.containsKey(filmId)) {
            throw new ObjectNotFoundException("Фильм с id " + id + " не найден");
        } else if (!films.get(filmId).getLikes().contains(userId)) {
            throw new ObjectNotFoundException("лайк от юзера с id " + id + " не найден для этого фильма");
        } else {
            films.get(filmId).getLikes().remove(userId);
            return films.get(filmId);
        }
    }

    @Override
    public List<Film> getBestFilms(int count) {
        log.info("Запрос Get /films/popular?count={count}" + count);
        LinkedList<Film> list = new LinkedList<>(films.values());
        list.sort((a, b) -> b.getLikes().size() - a.getLikes().size());
        while (list.size() > count) {
            list.removeLast();
        }
        return list;
    }

    boolean validate(Film film) throws ValidationException {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate releaseDate = LocalDate.parse(film.getReleaseDate(), inputFormatter);
        if (releaseDate.isBefore(MINDATA)) {
            log.debug("Введена неверная дата релиза (раньше 1895-12-28)");
            throw new ValidationException("Введена неверная дата релиза (раньше 1895-12-28)");
        }
        return true;
    }
}
