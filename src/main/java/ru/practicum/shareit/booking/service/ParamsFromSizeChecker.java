package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.error.BadRequestException;

@Component
public class ParamsFromSizeChecker extends EntryAccessor {
    @Override
    protected void check(InputRequest inputRequest) {
        if (inputRequest.getFrom() < 0 || inputRequest.getSize() < 0) {
            throw new BadRequestException(Constants.NEGATIVE_VALUE);
        }
    }
}