package ru.practicum.shareit.user.service;


import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.error.DuplicateEmailException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserService userService;
    @Mock
    private UserRepository userRepository;
    private final UserMapper userMapper = new UserMapperImpl();
    private User user1Id;
    private UserDto userDto1Id;
    private UserDto userDto2Id;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper);
        user1Id = new User()
                .setId(1L)
                .setName("Petr")
                .setEmail("petrov@mail.com");
        userDto1Id = new UserDto()
                .setId(1L)
                .setName("Petr")
                .setEmail("petrov@mail.com");
        userDto2Id = new UserDto()
                .setId(2L)
                .setName("Sergo")
                .setEmail("sergo@mail.com");
    }

    @Nested
    @DisplayName("---SAVE USER TESTS---")
    class SaveUserTest {
        @Test
        void saveUserNormalCase() {
            when(userRepository.save(any(User.class))).thenReturn(user1Id);
            UserDto userForResponse = userService.create(userDto1Id);

            assertNotNull(userForResponse);
            Assertions.assertEquals(userForResponse.getId(), 1L);
            Assertions.assertEquals(userForResponse.getName(), "Petr");
            Assertions.assertEquals(userForResponse.getEmail(), "petrov@mail.com");

            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("---UPDATE USER TESTS---")
    class UpdateUserTest {
        @Test
        void updateUserWithDuplicateEmail() {
            User user1Id = new User()
                    .setId(1L)
                    .setName("Dima")
                    .setEmail("dima@mail.com");

            UserDto userDto1Id = new UserDto()
                    .setName("Andrey")
                    .setEmail("hello@mail.com");

            UserDto userDto2Id = new UserDto()
                    .setName("Alex")
                    .setEmail("hello@mail.com");

            User user2Id = new User()
                    .setId(2L)
                    .setName("Vova")
                    .setEmail("vova@mail.com");
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1Id)).thenReturn(Optional.of(user2Id));
            when(userRepository.save(any(User.class))).thenReturn(user1Id);

            userService.update(1L, userDto1Id);

            DuplicateEmailException exception =
                    assertThrows(DuplicateEmailException.class, () -> userService.update(2L, userDto2Id));
            Assertions.assertEquals(exception.getMessage(), Constants.EMAIL_BUSY);

            verify(userRepository, times(2)).findById(anyLong());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void updateIfUserNotFound() {
            NotFoundException exception =
                    assertThrows(NotFoundException.class, () -> userService.update(1L, userDto1Id));
            Assertions.assertEquals(exception.getMessage(), Constants.USER_NOT_FOUND);

            verify(userRepository, times(1)).findById(anyLong());
        }

        @Test
        void updateUserNormalCase() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1Id));
            when(userRepository.save(any(User.class))).thenReturn(user1Id);
            UserDto updatedUser = userService.update(2L, userDto2Id);

            Assertions.assertEquals(updatedUser.getId(), userDto2Id.getId());
            Assertions.assertEquals(updatedUser.getName(), userDto2Id.getName());
            Assertions.assertEquals(updatedUser.getEmail(), userDto2Id.getEmail());

            verify(userRepository, times(1)).findById(anyLong());
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("---GET USER TESTS---")
    class GetUserTest {
        @Test
        void getUserById() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1Id));
            UserDto userDto = userService.getUserById(user1Id.getId());

            Assertions.assertEquals(userDto.getId(), user1Id.getId());
            Assertions.assertEquals(userDto.getName(), user1Id.getName());
            Assertions.assertEquals(userDto.getEmail(), user1Id.getEmail());

            verify(userRepository, times(1)).findById(anyLong());
        }

        @Test
        void getUserByWrongId() {
            NotFoundException exception =
                    assertThrows(NotFoundException.class, () -> userService.getUserById(99L));
            Assertions.assertEquals(exception.getMessage(), Constants.USER_NOT_FOUND);

            verify(userRepository, times(1)).findById(anyLong());
        }

        @Test
        void getAllUsersIfEmptyList() {
            List<UserDto> users = userService.getAllUsers();
            assertTrue(users.isEmpty());
        }

        @Test
        void getAllUsersNormalCase() {
            when(userRepository.findAll()).thenReturn(List.of(user1Id));
            List<UserDto> users = userService.getAllUsers();

            assertFalse(users.isEmpty());
            Assertions.assertEquals(users.get(0).getId(), user1Id.getId());
            Assertions.assertEquals(users.get(0).getName(), user1Id.getName());
            Assertions.assertEquals(users.get(0).getEmail(), user1Id.getEmail());
        }
    }

    @Nested
    @DisplayName("---DELETE USER TESTS---")
    class DeleteUserTest {
        @Test
        void deleteUserById() {
            when(userRepository.save(any(User.class))).thenReturn(user1Id);
            User user = userRepository.save(user1Id);
            userService.deleteUserById(user.getId());

            NotFoundException exception =
                    assertThrows(NotFoundException.class, () -> userService.getUserById(user.getId()));
            Assertions.assertEquals(exception.getMessage(), Constants.USER_NOT_FOUND);

            verify(userRepository, times(1)).deleteById(anyLong());
        }
    }
}