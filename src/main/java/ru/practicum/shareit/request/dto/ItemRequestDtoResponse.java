package ru.practicum.shareit.request.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Accessors(chain = true)
public class ItemRequestDtoResponse {
    @NotNull
    private Long id;
    @NotNull
    private String description;
    @NotNull
    private LocalDateTime created;
    @NotNull
    private List<Item> items;
}