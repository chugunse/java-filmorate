package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exeption.ObjectNotFoundException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        final String sqlQuery = "SELECT * FROM users";
        log.info("запрос чтения всех user отправлен");
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public User create(User user) {
        final String sqlQuery = "INSERT INTO users (email, login, name, birthday) VALUES ( ?, ?, ?, ?)";
        KeyHolder generatedId = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, generatedId);

        user.setId(generatedId.getKey().intValue());
        log.info("запрос создания user с id {} отправлен", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        checkUser(user.getId());
        final String sqlQuery = "UPDATE users SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getId());
        log.info("запрос на обновление user с id = {} отправлен.", user.getId());
        return user;
    }

    @Override
    public User getById(int id) {
        checkUser(id);
        final String sqlQuery = "SELECT * FROM users WHERE id = ?";
        log.info("запрос на получение user с id = {} отправлен.", id);
        return jdbcTemplate.queryForObject(sqlQuery, this::makeUser, id);
    }

    @Override
    public User deleteById(int id) {
        checkUser(id);
        final String sqlQuery = "DELETE FROM users WHERE id = ?";
        User user = jdbcTemplate.queryForObject(sqlQuery, this::makeUser, id);
        jdbcTemplate.update(sqlQuery, id);
        log.info("запрос на удаление user с id = {} отправлен", id);
        return user;
    }

    @Override
    public List<Integer> addFriendship(int firstId, int secondId) {
        checkUser(firstId);
        checkUser(secondId);
        final String sqlUpdateQuery = "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?";
        final String sqlWriteQuery = "INSERT INTO friends (user_id, friend_id, status ) VALUES (?, ?, ?)";
        final String checkQuery = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkQuery, firstId, secondId);
        if (userRows.next()) {
            jdbcTemplate.update(sqlUpdateQuery, true, firstId, secondId);
            log.info("user с id = {} подтвердил дружбу с user id = {}", firstId, secondId);
        } else {
            jdbcTemplate.update(sqlWriteQuery, firstId, secondId, false);
            log.info("user с id = {} подписался на user id = {}", firstId, secondId);
        }
        return List.of(firstId, secondId);
    }

    @Override
    public List<Integer> removeFriendship(int firstId, int secondId) {
        checkUser(firstId);
        checkUser(secondId);
        final String sqlDeleteQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        final String checkQuery = "SELECT status FROM friends WHERE user_id = ? AND friend_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkQuery, firstId, secondId);
        SqlRowSet userRowsRevers = jdbcTemplate.queryForRowSet(checkQuery, secondId, firstId);
        jdbcTemplate.update(sqlDeleteQuery, firstId, secondId);
        log.info("user с id = {} убрал из друзей user id = {}", firstId, secondId);
        return List.of(firstId, secondId);
    }

    @Override
    public List<User> getFriendsListById(int id) {
        checkUser(id);
        final String sqlQuery = "SELECT id, email, login, name, birthday FROM users " +
                "LEFT JOIN friends ON users.id = friends.friend_id " +
                "WHERE user_id = ?";
        log.info("запрос списка друзей user = {} отправлен", id);
        return jdbcTemplate.query(sqlQuery, this::makeUser, id);
    }

    @Override
    public List<User> getCommonFriendsList(int firstId, int secondId) {
        checkUser(firstId);
        checkUser(secondId);
        final String sqlQuery = "SELECT id, email, login, name, birthday FROM friends " +
                "LEFT JOIN users ON users.id = friends.friend_id " +
                "WHERE friends.user_id = ? AND friends.friend_id IN " +
                "( SELECT friend_id FROM friends " +
                "LEFT JOIN users ON users.id = friends.friend_id " +
                "WHERE friends.user_id = ? )";
        log.info("запрос списка общих друзей user = {} и user = {} отправлен", firstId, secondId);
        return jdbcTemplate.query(sqlQuery, this::makeUser, firstId, secondId);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }

    private void checkUser(int id) {
        final String sqlCheckQuery = "SELECT * FROM users WHERE id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlCheckQuery, id);
        if (!userRows.next()) {
            log.info("user с id = {} не найден.", id);
            throw new ObjectNotFoundException("user с id = " + id + " не найден.");
        }
    }
}
