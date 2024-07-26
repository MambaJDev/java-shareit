package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserAvailableChecker extends EntryAccessor {
    private final UserRepository userRepository;

    @Override
    protected void check(InputRequest inputRequest) {
        userRepository.findById(inputRequest.getBookerId()).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
    }
}