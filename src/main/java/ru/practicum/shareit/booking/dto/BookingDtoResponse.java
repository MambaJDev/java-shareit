package ru.practicum.shareit.booking.dto;


import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
public class BookingDtoResponse {
    @NotNull
    private Long id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    private Status status;
    @NotNull
    private ItemDto item;
    @NotNull
    private UserDto booker;

    @Setter
    @Getter
    public static class UserDto {
        @NotNull
        private Long id;
    }

    @Setter
    @Getter
    public static class ItemDto {
        @NotNull
        private Long id;
        @NotNull
        private String name;
    }
}