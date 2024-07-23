package ru.practicum.shareit.booking.service;


import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void saveBooking() {
        User owner = userRepository.save(new User()
                .setId(1L)
                .setName("Petr")
                .setEmail("petrov@mail.com"));
        userRepository.save(new User()
                .setId(2L)
                .setName("Sergo")
                .setEmail("sergo@mail.com"));

        Item item = itemRepository.save(new Item()
                .setId(1L)
                .setName("Кружка")
                .setOwner(owner)
                .setAvailable(true)
                .setDescription("Цвет синий, материал металл"));
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest()
                .setItemId(item.getId())
                .setStart(LocalDateTime.of(2024, 8, 14, 13, 0))
                .setEnd(LocalDateTime.of(2024, 8, 16, 13, 0));


        BookingDtoResponse bookingDtoResponse = bookingService.save(2L, bookingDtoRequest);

        assertNotNull(bookingDtoResponse);
        Assertions.assertEquals(bookingDtoResponse.getId(), 1L);
        Assertions.assertEquals(bookingDtoResponse.getItem().getId(), 1L);
        Assertions.assertEquals(bookingDtoResponse.getItem().getName(), "Кружка");
        Assertions.assertEquals(bookingDtoResponse.getStatus(), Status.WAITING);
        Assertions.assertEquals(bookingDtoResponse.getBooker().getId(), 2L);
        Assertions.assertEquals(bookingDtoResponse.getStart().toString(), "2024-08-14T13:00");
        Assertions.assertEquals(bookingDtoResponse.getEnd().toString(), "2024-08-16T13:00");
    }
}