package ru.practicum.shareit.booking.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 2);
        owner = userRepository.save(new User()
                .setId(1L)
                .setName("Петр")
                .setEmail("petr@mail.ru"));
        booker = userRepository.save(new User()
                .setId(2L)
                .setName("Vasya")
                .setEmail("vasil@mail.ru"));

        item = itemRepository.save(new Item()
                .setId(1L)
                .setName("Кружка")
                .setOwner(owner)
                .setAvailable(true)
                .setDescription("Цвет синий, материал металл"));
        booking = bookingRepository.save(new Booking()
                .setId(1L)
                .setStatus(Status.WAITING)
                .setBooker(booker)
                .setItem(item)
                .setStart(LocalDateTime.now())
                .setEnd(LocalDateTime.now().plusDays(1)));
    }

    @Nested
    @DisplayName("--- TESTS BOOKINGS BY ITEM ---")
    class ItemBookings {
        @Test
        @DisplayName("find all bookings by item")
        void findALLByItem() {
            List<Booking> bookings = bookingRepository.findALLByItem(item);

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0), booking);
        }

        @Test
        @DisplayName("find all past bookings by booker id and item id")
        void findAllByItemIdAndBookerIdAndEndIsBefore() {
            booking.setStart(LocalDateTime.now().minusDays(2));
            booking.setEnd(LocalDateTime.now().minusMinutes(1));
            List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndEndIsBefore(item.getId(), booker.getId(), LocalDateTime.now());

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0), booking);
        }
    }

    @Nested
    @DisplayName("--- TESTS BOOKINGS BY BOOKER ---")
    class BookerBookings {

        @Test
        @DisplayName("find all bookings by booker id")
        void findAllByBookerIdOrderByStartDesc() {
            List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), pageable);

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0), booking);
        }

        @Test()
        @DisplayName("find all future bookings by booker id")
        void findAllByBookerIdAndStartIsAfterOrderByStartDesc() {
            booking.setStart(LocalDateTime.now().plusMinutes(1));
            List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(booker.getId(), LocalDateTime.now(), pageable);

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0), booking);
        }

        @Test
        @DisplayName("find all past bookings by booker id")
        void findAllByBookerIdAndEndIsBeforeOrderByStartDesc() {
            booking.setStart(LocalDateTime.now().minusDays(2));
            booking.setEnd(LocalDateTime.now().minusMinutes(1));
            List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(booker.getId(), LocalDateTime.now(), pageable);

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0), booking);
        }

        @Test
        @DisplayName("find all current bookings by booker id")
        void findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
            booking.setStart(LocalDateTime.now().minusMinutes(1));
            booking.setEnd(LocalDateTime.now().plusMinutes(1));
            List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(booker.getId(), LocalDateTime.now(), LocalDateTime.now(), pageable);

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0), booking);
        }

        @Test
        @DisplayName("find all bookings by booker id with status 'waiting'")
        void findAllByBookerIdAndStatusIsWaiting() {
            List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.WAITING, pageable);

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0), booking);
        }

        @Test
        @DisplayName("find all bookings by booker id with status not 'waiting'")
        void findAllByBookerIdAndStatusIsNotWaiting() {
            List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.REJECTED, pageable);

            assertTrue(bookings.isEmpty());
        }
    }

    @Nested
    @DisplayName("--- TESTS BOOKINGS BY OWNER ---")
    class OwnerBookings {

        @Test
        @DisplayName("find all bookings by owner id")
        void findAllByItemOwnerIdOrderByStartDesc() {
            List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId(), pageable);

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0), booking);
        }

        @Test
        @DisplayName("find all future bookings by owner id")
        void findALLByItemOwnerIdAndStartIsAfterOrderByStartDesc() {
            booking.setStart(LocalDateTime.now().plusMinutes(1));
            List<Booking> bookings = bookingRepository.findALLByItemOwnerIdAndStartIsAfterOrderByStartDesc(owner.getId(), LocalDateTime.now(), pageable);

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0), booking);
        }

        @Test
        @DisplayName("find all past bookings by owner id")
        void findALLByItemOwnerIdAndEndIsBeforeOrderByStartDesc() {
            booking.setStart(LocalDateTime.now().minusDays(2));
            booking.setEnd(LocalDateTime.now().minusMinutes(1));
            List<Booking> bookings = bookingRepository.findALLByItemOwnerIdAndEndIsBeforeOrderByStartDesc(owner.getId(), LocalDateTime.now(), pageable);

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0), booking);
        }

        @Test
        @DisplayName("find all current bookings by owner id")
        void findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
            booking.setStart(LocalDateTime.now().minusMinutes(1));
            booking.setEnd(LocalDateTime.now().plusMinutes(1));
            List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(owner.getId(), LocalDateTime.now(), LocalDateTime.now(), pageable);

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0), booking);
        }

        @Test
        @DisplayName("find all bookings by booker id with status 'waiting'")
        void findAllByItemOwnerIdAndStatusStatusIsWaiting() {
            List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.WAITING, pageable);

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0), booking);
        }

        @Test
        @DisplayName("find all bookings by booker id with status not 'waiting'")
        void findAllByItemOwnerIdAndStatusStatusIsNotWaiting() {
            List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.REJECTED, pageable);

            assertTrue(bookings.isEmpty());
        }
    }
}