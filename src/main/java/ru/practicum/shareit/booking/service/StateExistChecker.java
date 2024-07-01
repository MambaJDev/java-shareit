package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.constant.Constants;

import java.util.Arrays;

@Component
public class StateExistChecker extends EntryAccessor {
    @Override
    protected void check(Long bookerId, State state) {
        if (Arrays.stream(State.values()).noneMatch(element -> element.equals(state))) {
            throw new IllegalArgumentException(Constants.WRONG_STATE);
        }
    }
}