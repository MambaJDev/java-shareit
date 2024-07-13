package ru.practicum.shareit.request.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ItemRequestDto {
    @NotNull
    private String description;
}