package ru.practicum.shareit.booking.dto;


import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BookingDtoRequest {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}