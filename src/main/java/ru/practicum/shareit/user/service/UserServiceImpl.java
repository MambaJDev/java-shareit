package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        User userFromDto = userMapper.toUser(userDto);
        userRepository.checkDublicateEmail(userFromDto);
        return userMapper.toUserDto(userRepository.create(userFromDto));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        checkUserIsPresent(id);
        User userFromDto = userMapper.toUser(userDto);
        User userFromRepository = userRepository.getUserById(id);
        if (userFromDto.getEmail() != null && !userFromDto.getEmail().equals(userFromRepository.getEmail())) {
            userRepository.checkDublicateEmail(userFromDto);
            userRepository.getAllEmails().remove(userFromRepository.getEmail());
            userRepository.getAllEmails().add(userFromDto.getEmail());
        }
        userMapper.updateUserFromUserDto(userDto, userFromRepository);
        userRepository.update(userFromRepository);
        return userMapper.toUserDto(userFromRepository);
    }

    @Override
    public UserDto getUserById(Long id) {
        checkUserIsPresent(id);
        User user = userRepository.getUserById(id);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
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