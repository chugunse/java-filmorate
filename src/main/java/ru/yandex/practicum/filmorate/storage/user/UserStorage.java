package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exeption.ValidationException;

import java.util.List;

public interface UserStorage {

    List<User> findAll();

    User create(User user) throws ValidationException;

    User update(User user) throws Exception;

    User getById(int id) throws Exception;

    User deleteById(int id) throws Exception;

    List<Integer> addFriendship(int firstId, int secondId);

    List<Integer> removeFriendship(int firstId, int secondId);

    List<User> getFriendsListById(int id);

    List<User> getCommonFriendsList(int firstId, int secondId);
}
