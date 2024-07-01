package ru.practicum.shareit.item.dto;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
public class CommentDto {
    @NotBlank
    @Length(max = 255)
    private String text;
}