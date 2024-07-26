package ru.practicum.shareit.user.dto;


import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserDto {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String email;
}