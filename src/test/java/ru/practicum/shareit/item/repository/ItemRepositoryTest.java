package ru.practicum.shareit.item.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;
    private User user;
    private Item item1;
    private Item item2;
    private Pageable page;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        page = PageRequest.of(0, 2);
        user = userRepository.save(new User()
                .setId(1L)
                .setName("Петр")
                .setEmail("petr@mail.ru"));

        itemRequest = requestRepository.save(new ItemRequest()
                .setId(1L)
                .setRequester(user)
                .setCreated(LocalDateTime.now())
                .setDescription("Ищу тарелку керамическую"));

        item1 = itemRepository.save(new Item()
                .setId(1L)
                .setName("Кружка")
                .setOwner(user)
                .setAvailable(true)
                .setDescription("Цвет синий, материал металл"));
        item2 = itemRepository.save(new Item()
                .setId(2L)
                .setName("Тарелка")
                .setOwner(user)
                .setAvailable(true)
                .setDescription("Цвет желтый, материал керамика")
                .setRequestId(itemRequest.getId()));
    }

    @Test
    void findAllByOwnerIdOrderById() {
        List<Item> expectedItemList = List.of(item1, item2);
        List<Item> actualItemList = itemRepository.findAllByOwnerIdOrderById(user.getId(), page);

        assertIterableEquals(actualItemList, expectedItemList);
    }

    @Test
    void findAllItemsByNotOwner() {
        List<Item> expectedItemList = List.of(item1, item2);
        List<Item> actualItemList = itemRepository.findAllByOwnerIdOrderById(2L, page);

        assertNotNull(actualItemList);
        assertTrue(actualItemList.isEmpty());
        assertNotEquals(actualItemList, expectedItemList);
    }

    @Test
    void findByNameIgnoreCaseContainingAndAvailableTrueOrDescriptionIgnoreCaseContainingAndAvailableTrue() {
        List<Item> expectedItemList = List.of(item1, item2);
        List<Item> actualItemList =
                itemRepository.findByNameIgnoreCaseContainingAndAvailableTrueOrDescriptionIgnoreCaseContainingAndAvailableTrue(
                        "вет", "вет", page);

        assertIterableEquals(actualItemList, expectedItemList);
    }

    @Test
    void findAllByRequestId() {
        List<Item> actualItemList = itemRepository.findAllByRequestId(itemRequest.getId());

        assertEquals(actualItemList.get(0), item2);
        assertEquals(actualItemList.get(0).getName(), item2.getName());
    }
}