package ru.practicum.shareit.user.service;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserShort;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserShort userShort);
}