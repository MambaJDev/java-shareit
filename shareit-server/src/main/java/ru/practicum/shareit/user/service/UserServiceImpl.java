package ru.practicum.shareit.user.service;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.error.DuplicateEmailException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Setter
@Getter
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private Set<String> emails = new HashSet<>();

    @Override
    public UserDto create(UserDto userDto) {
        emails.add(userDto.getEmail());
        User user = userRepository.save(userMapper.toUser(userDto));
        log.info("Юзер с id = {} добавлен", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            checkDuplicateEmail(userDto.getEmail());
            emails.remove(user.getEmail());
            emails.add(userDto.getEmail());
        }
        userMapper.updateUserFromUserDto(userDto, user);
        User userResponse = userRepository.save(user);
        log.info("Юзер с id = {} обновлен", user.getId());
        return userMapper.toUserDto(userResponse);
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException(Constants.USER_NOT_FOUND);
        }
        return userMapper.toUserDto(user.get());
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private void checkDuplicateEmail(String email) {
        if (emails.contains(email)) {
            throw new DuplicateEmailException(Constants.EMAIL_BUSY);
        }
    }
}