package ru.practicum.gateway.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserDto {
    private Long id;
    @NotNull(groups = NewUser.class)
    private String name;
    @Email(groups = {NewUser.class, UpdateUser.class})
    @NotNull(groups = NewUser.class)
    private String email;
}