package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.NewUser;
import ru.practicum.shareit.user.dto.UpdateUser;
import ru.practicum.shareit.user.dto.UserShort;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public User create(@Validated(NewUser.class)
                       @RequestBody UserShort userShort) {
        log.info("Поступил Post-запрос на добавление User");
        return userService.create(userShort);
    }

    @PatchMapping("/{id}")
    public User update(@Validated(UpdateUser.class)
                       @RequestBody UserShort userShort,
                       @PathVariable Long id) {
        log.info("Поступил Patch-запрос на обновление User с ID = {}", id);
        return userService.update(id, userShort);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Поступил GET-запрос на получение User c ID = {}", id);
        return userService.getUserById(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Поступил GET-запрос на получение всех Users ");
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        log.info("Поступил DELETE-запрос на удаление User с id = {}", id);
        userService.deleteUserById(id);
    }
}