package ru.practicum.shareit.request.dto;


import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.shareit.item.model.Item;

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