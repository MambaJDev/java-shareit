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
    private static Long idGenerator = 1L;
    private final Set<String> emails = new HashSet<>();

    @Override
    public User create(User user) {
        checkDublicateEmail(user);
        emails.add(user.getEmail());
        user.setId(idGenerator++);
        users.put(user.getId(), user);
        log.info("Юзер с id = {} добавлен", user.getId());
        return user;
    }

    private void checkDublicateEmail(User user) {
        if (emails.contains(user.getEmail())) {
            log.info("Email уже существует");
            throw new DublicateEmailException("this email is already taken");
        }
    }

    @Override
    public User update(Long id, User user) {
        User userFromMap = users.get(id);
        if (user.getName() != null) {
            userFromMap.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().equals(userFromMap.getEmail())) {
            checkDublicateEmail(user);
            emails.remove(userFromMap.getEmail());
            emails.add(user.getEmail());
            userFromMap.setEmail(user.getEmail());
        }
        log.info("Юзер с id = {} обновлен", id);
        return userFromMap;
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
        log.info("Юзер с id = {} удален", id);
        emails.remove(users.remove(id).getEmail());
    }
}