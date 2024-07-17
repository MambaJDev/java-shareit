package ru.practicum.shareit.request.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestServiceImplIntegrationTest {
    @Autowired
    private RequestService requestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    @Test
    void getAllByRequesterId() {
        User requester = userRepository.save(new User()
                .setName("Petr")
                .setEmail("petrov@mail.com"));
        requestRepository.save(new ItemRequest()
                .setRequester(requester)
                .setDescription("Ищу кружку")
                .setCreated(LocalDateTime.of(2024, 7, 14, 13, 0)));

        List<ItemRequestDtoResponse> requestDtoResponseList = requestService.getAllByRequesterId(requester.getId());
        assertFalse(requestDtoResponseList.isEmpty());
        assertEquals(requestDtoResponseList.get(0).getId(), 1L);
        assertEquals(requestDtoResponseList.get(0).getDescription(), "Ищу кружку");
        assertEquals(requestDtoResponseList.get(0).getCreated().toString(), "2024-07-14T13:00");
    }
}