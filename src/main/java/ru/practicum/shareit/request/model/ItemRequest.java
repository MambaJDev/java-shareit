package ru.practicum.shareit.request.model;

import lombok.Getter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
public class ItemRequest {
    private Long id;
    private String description;
    private User requestor;
    private final LocalDate created = LocalDate.now();
}