package ru.practicum.shareit.request.repository;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class RequestRepositoryTest {
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    private User requester;
    private User notRequester;
    private Pageable pageable;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 2);
        requester = userRepository.save(new User()
                .setId(1L)
                .setName("Петр")
                .setEmail("petr@mail.ru"));

        notRequester = userRepository.save(new User()
                .setId(2L)
                .setName("Андрей")
                .setEmail("and@mail.ru"));


        itemRequest = requestRepository.save(new ItemRequest()
                .setId(1L)
                .setRequester(requester)
                .setCreated(LocalDateTime.now())
                .setDescription("Ищу посуду"));
    }

    @Test
    void findAllByNotRequester() {
        List<ItemRequest> itemRequests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(notRequester.getId());

        assertTrue(itemRequests.isEmpty());
    }

    @Test
    void findAllOwnRequestsWhenNormalCase() {
        List<ItemRequest> itemRequests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(requester.getId());

        assertFalse(itemRequests.isEmpty());
        assertEquals(itemRequests.size(), 1);
        Assertions.assertEquals(itemRequests.get(0), itemRequest);
    }

    @Test
    void findAllOtherRequestsByRequester() {
        List<ItemRequest> itemRequests = requestRepository.findAllStrangerRequests(requester.getId(), pageable);

        assertTrue(itemRequests.isEmpty());
    }

    @Test
    void findAllOtherRequestsByNotRequester() {
        List<ItemRequest> itemRequests = requestRepository.findAllStrangerRequests(notRequester.getId(), pageable);

        assertFalse(itemRequests.isEmpty());
        assertEquals(itemRequests.size(), 1);
        Assertions.assertEquals(itemRequests.get(0), itemRequest);
    }
}