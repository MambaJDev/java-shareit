package ru.practicum.shareit.request.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Accessors(chain = true)
public class ItemRequestDto {
    @NotNull
    private String description;
}