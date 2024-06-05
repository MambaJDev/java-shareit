package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Set;

public interface UserRepository {
    User create(User user);

    User update(User user);

    User getUserById(Long id);

    List<User> getAllUsers();

    void deleteUserById(Long id);

    void checkDublicateEmail(User user);

    Set<String> getAllEmails();
}