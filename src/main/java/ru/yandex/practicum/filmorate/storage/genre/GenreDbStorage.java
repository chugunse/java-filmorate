package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.exeption.ObjectNotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAll() {
        final String sqlQuery = "SELECT genre_id, genre_name FROM genre ";
        log.info("запрос всех жанров выполнен");
        return jdbcTemplate.query(sqlQuery, this::makeGenre);
    }

    @Override
    public Genre getById(int id) {
        final String sqlQuery = "SELECT genre_id, genre_name FROM genre WHERE genre_id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (!genreRows.next()) {
            log.info("Жанр с id = {} не найден.", id);
            throw new ObjectNotFoundException("Жанр не найден");
        }
        return jdbcTemplate.queryForObject(sqlQuery, this::makeGenre, id);
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        int genreId = rs.getInt("genre_id");
        String genreName = rs.getString("genre_name");
        return new Genre(genreId, genreName);
    }
}
