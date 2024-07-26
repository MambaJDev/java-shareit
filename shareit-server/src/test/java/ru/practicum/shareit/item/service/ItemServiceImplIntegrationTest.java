package ru.practicum.shareit.item.service;


import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;


    @Test
    void getUserItems() {
        User booker = userRepository.save(new User()
                .setName("Sergo")
                .setEmail("sergo@mail.ru"));
        User owner = userRepository.save(new User()
                .setName("Petr")
                .setEmail("petr@mail.ru"));
        Item item = itemRepository.save(new Item()
                .setName("Кружка")
                .setOwner(owner)
                .setAvailable(true)
                .setDescription("Цвет синий, материал металл"));
        Booking lastBooking = bookingRepository.save(new Booking()
                .setStatus(Status.APPROVED)
                .setBooker(booker)
                .setItem(item)
                .setStart(LocalDateTime.now().minusSeconds(2))
                .setEnd(LocalDateTime.now().minusSeconds(1)));
        Booking nextBooking = bookingRepository.save(new Booking()
                .setStatus(Status.WAITING)
                .setBooker(booker)
                .setItem(item)
                .setStart(LocalDateTime.now().plusSeconds(1))
                .setEnd(LocalDateTime.now().plusSeconds(2)));
        List<ItemDtoResponse> items = itemService.getAll(owner.getId(), 0, 2);

        assertFalse(items.isEmpty());
        Assertions.assertEquals(items.get(0).getId(), item.getId());
        Assertions.assertEquals(items.get(0).getName(), "Кружка");
        Assertions.assertEquals(items.get(0).getDescription(), "Цвет синий, материал металл");
        Assertions.assertEquals(items.get(0).getLastBooking().getBookerId(), booker.getId());
        Assertions.assertEquals(items.get(0).getLastBooking().getId(), lastBooking.getId());
        Assertions.assertEquals(items.get(0).getNextBooking().getBookerId(), booker.getId());
        Assertions.assertEquals(items.get(0).getNextBooking().getId(), nextBooking.getId());
        Assertions.assertEquals(items.get(0).getAvailable(), true);
    }
}