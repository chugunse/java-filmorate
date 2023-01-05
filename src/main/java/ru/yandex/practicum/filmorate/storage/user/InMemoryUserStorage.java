package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exeption.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.exeption.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int id = 0;
    private final Map<Integer, User> users = new HashMap<>();

    private int genId() {
        return ++id;
    }

    @Override
    public List<User> findAll() {
        log.debug("Текущее количество юзеров: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) throws ValidationException {
        log.info("Запрос POST /users " + user);
        if (validate(user)) {
            user.setId(genId());
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public User update(User user) throws Exception {
        log.info("Запрос PUT /users " + user);
        validate(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new ObjectNotFoundException("User с id " + id + " не найден");
        }
        return user;
    }

    @Override
    public User getById(int id) {
        log.info("Запрос Get /users " + id);
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new ObjectNotFoundException("User с id " + id + " не найден");
        }
    }

    @Override
    public User deleteById(int id){
        log.info("Запрос Del /users " + id);
        if (users.containsKey(id)) {
            User user = users.get(id);
            users.remove(id);
            return user;
        } else {
            throw new ObjectNotFoundException("User с id " + id + " не найден");
        }
    }

    @Override
    public List<Integer> addFriendship(int firstId, int secondId) {
        log.info("Запрос PUT /users/{id}/friends/{friendId} " + firstId + " " + secondId);
        if (!users.containsKey(firstId)) {
            throw new ObjectNotFoundException("User с id " + firstId + " не найден");
        } else if (!users.containsKey(secondId)) {
            throw new ObjectNotFoundException("User с id " + secondId + " не найден");
        } else {
            users.get(firstId).getFriends().add(secondId);
            users.get(secondId).getFriends().add(firstId);
            return new ArrayList<>(users.get(firstId).getFriends());
        }
    }

    @Override
    public List<Integer> removeFriendship(int firstId, int secondId) {
        log.info("Запрос DELETE /users/{id}/friends/{friendId} " + firstId + " " + secondId);
        users.get(firstId).getFriends().remove(secondId);
        users.get(secondId).getFriends().remove(firstId);
        return new ArrayList<>(users.get(firstId).getFriends());
    }

    @Override
    public List<User> getFriendsListById(int id) {
        log.info("Запрос GET /users/{id}/friends " + id);
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("User с id " + id + " не найден");
        } else {
            List<User> list = new ArrayList<>();
            for (int friendsId : users.get(id).getFriends()) {
                list.add(users.get(friendsId));
            }
            return list;
        }
    }

    @Override
    public List<User> getCommonFriendsList(int firstId, int secondId) {
        log.info("Запрос GET /users/{id}/friends/common/{otherId} " + firstId + " " + secondId);
        List<User> list = new ArrayList<>();
        for (int id : users.get(firstId).getFriends()) {
            if (users.get(secondId).getFriends().contains(id)) {
                list.add(users.get(id));
            }
        }
        return list;
    }

    boolean validate(User user) throws ValidationException {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthday = LocalDate.parse(user.getBirthday(), inputFormatter);
        if (birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("введенная дата дня рождения еще не наступила");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return true;
    }
}
