package ru.practicum.gateway.item.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class CommentDto {
    @NotBlank
    @Length(max = 255)
    private String text;
}