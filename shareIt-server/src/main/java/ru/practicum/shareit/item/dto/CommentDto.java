package ru.practicum.shareit.item.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CommentDto {
    private String text;
}