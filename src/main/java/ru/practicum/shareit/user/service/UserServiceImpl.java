package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserShort;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public User create(UserShort userShort) {
        return userRepository.create(userMapper.toUser(userShort));
    }

    @Override
    public User update(Long id, UserShort userShort) {
        checkUserIsPresent(id);
        return userRepository.update(id, userMapper.toUser(userShort));
    }

    @Override
    public User getUserById(Long id) {
        checkUserIsPresent(id);
        return userRepository.getUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public void deleteUserById(Long id) {
        checkUserIsPresent(id);
        userRepository.deleteUserById(id);
    }

    private void checkUserIsPresent(Long userId) {
        if (userId == null || userRepository.getUserById(userId) == null) {
            log.info("User с ID = {} не найден", userId);
            throw new IllegalArgumentException("User not found");
        }
    }
}