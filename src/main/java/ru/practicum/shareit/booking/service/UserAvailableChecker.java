package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
@AllArgsConstructor
public class UserAvailableChecker extends EntryAccessor {
    UserRepository userRepository;

    @Override
    protected void check(Long bookerId, State state) {
        userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
    }
}