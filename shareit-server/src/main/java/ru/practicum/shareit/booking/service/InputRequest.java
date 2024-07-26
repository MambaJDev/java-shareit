package ru.practicum.shareit.booking.service;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.State;

@Component
@Accessors(chain = true)
@Getter
@Setter
public class InputRequest {
    private Long bookerId;
    private State state;
    private Integer from;
    private Integer size;
}