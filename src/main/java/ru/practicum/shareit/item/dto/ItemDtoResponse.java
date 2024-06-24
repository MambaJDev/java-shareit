package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ItemDtoResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDtoResponse> comments;

    @AllArgsConstructor
    @Getter
    @Setter
    public static class BookingDto {
        @NotNull
        private Long id;
        @NotNull
        private Long bookerId;
    }
}