package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

   UserDto update(Long id, UserDto userDto);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    void deleteUserById(Long id);
}