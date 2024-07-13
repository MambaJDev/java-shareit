package ru.practicum.shareit.user.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;


    @Test
    void saveUserWithDuplicateEmailTest() {
        User user = new User()
                .setId(1L)
                .setName("Петр")
                .setEmail("petr@mail.ru");
        User userWithDuplicateEmail = new User()
                .setId(2L)
                .setName("Sergo")
                .setEmail("petr@mail.ru");
        userRepository.save(user);

        DataIntegrityViolationException exception =
                assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(userWithDuplicateEmail));
    }
}