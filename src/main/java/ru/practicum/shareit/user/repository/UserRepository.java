package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User create(User user);

    User update(Long id, User user);

    User getUserById(Long id);

    List<User> getAllUsers();

    void deleteUserById(Long id);
}