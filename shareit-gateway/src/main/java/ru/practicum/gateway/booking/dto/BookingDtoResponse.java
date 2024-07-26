package ru.practicum.gateway.booking.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
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