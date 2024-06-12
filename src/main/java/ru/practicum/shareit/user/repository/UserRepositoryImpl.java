package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.DublicateEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long idGenerator = 1L;

    @Override
    public User create(User user) {
        emails.add(user.getEmail());
        user.setId(idGenerator++);
        users.put(user.getId(), user);
        log.info("Юзер с id = {} добавлен", user.getId());
        return user;
    }

    @Override
    public void checkDublicateEmail(User user) {
        if (emails.contains(user.getEmail())) {
            log.info("Email уже существует");
            throw new DublicateEmailException("this email is already taken");
        }
    }

    @Override
    public Set<String> getAllEmails() {
        return emails;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Получен список Users размер списка = {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUserById(Long id) {
        User user = users.remove(id);
        log.info("Юзер с id = {} удален", id);
        emails.remove(user.getEmail());
    }
}