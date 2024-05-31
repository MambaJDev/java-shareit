package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserShort;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User create(UserShort userShort);

    User update(Long id, UserShort userShort);

    User getUserById(Long id);

    List<User> getAllUsers();

    void deleteUserById(Long id);
}