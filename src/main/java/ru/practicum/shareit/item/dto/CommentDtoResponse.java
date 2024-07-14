package ru.practicum.shareit.item.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentDtoResponse {
    @NotNull
    private Long id;
    @NotNull
    private String text;
    @NotNull
    private String authorName;
    @NotNull
    private LocalDateTime created;
}