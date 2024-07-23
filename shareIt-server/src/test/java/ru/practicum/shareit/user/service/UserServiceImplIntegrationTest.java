package ru.practicum.shareit.user.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;

    @Test
    void createUser() {
        UserDto userDto = new UserDto()
                .setName("Dima")
                .setEmail("dima@mail.com");

        UserDto userResponse = userService.create(userDto);
        assertNotNull(userResponse);
        Assertions.assertEquals(userResponse.getId(), 1L);
        Assertions.assertEquals(userResponse.getName(), "Dima");
        Assertions.assertEquals(userResponse.getEmail(), "dima@mail.com");
    }
}