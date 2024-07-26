package ru.practicum.shareit.booking.service;


import java.time.LocalDateTime;
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
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.error.BadRequestException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private final InputRequest inputRequest = new InputRequest();

    private final BookingMapper bookingMapper = new BookingMapperImpl();
    private User booker;
    private BookingDtoRequest bookingDtoRequest;
    private User owner;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        UserAvailableChecker userAvailableChecker = new UserAvailableChecker(userRepository);
        bookingService = new BookingServiceImpl(
                bookingMapper,
                bookingRepository,
                userRepository,
                itemRepository,
                userAvailableChecker,
                inputRequest);

        owner = new User()
                .setId(1L)
                .setName("Petr")
                .setEmail("petrov@mail.com");
        booker = new User()
                .setId(2L)
                .setName("Sergo")
                .setEmail("sergo@mail.com");
        item = new Item()
                .setId(1L)
                .setName("Кружка")
                .setOwner(owner)
                .setAvailable(true)
                .setDescription("Цвет синий, материал металл");
        bookingDtoRequest = new BookingDtoRequest()
                .setItemId(1L)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(2));
        booking = new Booking()
                .setId(1L)
                .setStatus(Status.WAITING)
                .setBooker(booker)
                .setItem(item)
                .setStart(LocalDateTime.now().plusSeconds(1))
                .setEnd(LocalDateTime.now().plusSeconds(2));
    }

    @Nested
    @DisplayName("---SAVE BOOKING TESTS---")
    class SaveBookingTest {
        @Test
        void saveBookingWhenNormalCase() {
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

            BookingDtoResponse bookingDtoResponse = bookingService.save(booker.getId(), bookingDtoRequest);

            groupAssertChecking(bookingDtoResponse, booking);
        }

        @Test
        void saveBookingWhenUserNotFound() {
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            NotFoundException exception = assertThrows(
                    NotFoundException.class, () -> bookingService.save(booker.getId(), bookingDtoRequest));
            Assertions.assertEquals(exception.getMessage(), Constants.USER_NOT_FOUND);
        }

        @Test
        void saveBookingWhenItemNotFound() {
            NotFoundException exception = assertThrows(
                    NotFoundException.class, () -> bookingService.save(booker.getId(), bookingDtoRequest));
            Assertions.assertEquals(exception.getMessage(), Constants.ITEM_NOT_FOUND);
        }

        @Test
        void saveBookingWhenBookerIsOwner() {
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

            NotFoundException exception = assertThrows(
                    NotFoundException.class, () -> bookingService.save(owner.getId(), bookingDtoRequest));
            Assertions.assertEquals(exception.getMessage(), Constants.OWNER_CAN_NOT_BOOK);
        }

        @Test
        void saveBookingWhenItemNotAvailable() {
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            item.setAvailable(false);

            BadRequestException exception = assertThrows(
                    BadRequestException.class, () -> bookingService.save(booker.getId(), bookingDtoRequest));
            Assertions.assertEquals(exception.getMessage(), Constants.NOT_AVAILABLE_ITEM);
        }
    }

    @Nested
    @DisplayName("---GET BOOKING BY ID TESTS---")
    class GetBookingByIdTest {

        @Test
        void getBookingByIdWhenNormalCase() {
            when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
            BookingDtoResponse bookingDtoResponse = bookingService.getBookingById(booker.getId(), booking.getId());

            groupAssertChecking(bookingDtoResponse, booking);
        }

        @Test
        void getBookingByIdWhenBookingNotFound() {
            NotFoundException exception = assertThrows(
                    NotFoundException.class, () -> bookingService.getBookingById(booker.getId(), booking.getId()));
            Assertions.assertEquals(exception.getMessage(), Constants.BOOKING_NOT_FOUND);
        }

        @Test
        void getBookingByIdWhenUserCantBook() {
            when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

            NotFoundException exception = assertThrows(
                    NotFoundException.class, () -> bookingService.getBookingById(3L, booking.getId()));
            Assertions.assertEquals(exception.getMessage(), Constants.USER_CAN_NOT_GET_ITEM);
        }
    }

    @Nested
    @DisplayName("---APPROVE BOOKING TESTS---")
    class ApproveBookingTest {
        @Test
        void approveBookingWhenNormalCase() {
            when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
            BookingDtoResponse bookingDtoResponse = bookingService.approveBooking(owner.getId(), booking.getId(), true);

            groupAssertChecking(bookingDtoResponse, booking);
        }

        @Test
        void approveBookingWhenNotOwner() {
            when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

            NotFoundException exception = assertThrows(
                    NotFoundException.class, () -> bookingService.approveBooking(booker.getId(), booking.getId(), true));
            Assertions.assertEquals(exception.getMessage(), Constants.USER_NOT_OWNER);
        }

        @Test
        void approveBookingWhenAlreadyApproved() {
            booking.setStatus(Status.APPROVED);
            when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
            BadRequestException exception = assertThrows(
                    BadRequestException.class, () -> bookingService.approveBooking(owner.getId(), booking.getId(), true));
            Assertions.assertEquals(exception.getMessage(), Constants.APPROVED_ITEM);
        }

        @Test
        void rejectBooking() {
            when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
            BookingDtoResponse bookingDtoResponse = bookingService.approveBooking(owner.getId(), booking.getId(), false);

            groupAssertChecking(bookingDtoResponse, booking);
        }
    }

    @Nested
    @DisplayName("---GET ALL BOOKING BY USER TESTS---")
    class GetAllBookingsByUserTest {
        @Test
        void getAllBookingsByUserId() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
            when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));
            List<BookingDtoResponse> bookings = bookingService.getAllBookingsByUserId(booker.getId(), State.ALL, 0, 2);

            groupAssertChecking(bookings.get(0), booking);
        }

        @Test
        void getAllFutureBookingsByUserId() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
            when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(
                    anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));
            List<BookingDtoResponse> bookings = bookingService.getAllBookingsByUserId(booker.getId(), State.FUTURE, 0, 2);

            groupAssertChecking(bookings.get(0), booking);
        }

        @Test
        void getAllPastBookingsByUserId() {
            booking.setStart(LocalDateTime.now().minusSeconds(2));
            booking.setEnd(LocalDateTime.now().minusSeconds(1));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
            when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(
                    anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));
            List<BookingDtoResponse> bookings = bookingService.getAllBookingsByUserId(booker.getId(), State.PAST, 0, 2);

            groupAssertChecking(bookings.get(0), booking);
        }

        @Test
        void getAllCurrentBookingsByUserId() {
            booking.setStart(LocalDateTime.now().minusSeconds(1));
            booking.setEnd(LocalDateTime.now().plusSeconds(1));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
            when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartAsc(
                    anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));
            List<BookingDtoResponse> bookings = bookingService.getAllBookingsByUserId(booker.getId(), State.CURRENT, 0, 2);

            groupAssertChecking(bookings.get(0), booking);
        }

        @Test
        void getAllWaitingBookingsByUserId() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
            when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                    anyLong(), any(Status.class), any(Pageable.class))).thenReturn(List.of(booking));
            List<BookingDtoResponse> bookings = bookingService.getAllBookingsByUserId(booker.getId(), State.WAITING, 0, 2);

            groupAssertChecking(bookings.get(0), booking);
        }

        @Test
        void getAllRejectedBookingsByUserId() {
            booking.setStatus(Status.REJECTED);
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
            when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                    anyLong(), any(Status.class), any(Pageable.class))).thenReturn(List.of(booking));
            List<BookingDtoResponse> bookings = bookingService.getAllBookingsByUserId(booker.getId(), State.REJECTED, 0, 2);

            groupAssertChecking(bookings.get(0), booking);
        }

        @Test
        void getAllBookingsByUserIdWhenUserNotFound() {
            NotFoundException exception = assertThrows(
                    NotFoundException.class, () -> bookingService.getAllBookingsByUserId(booker.getId(), State.WAITING, 0, 2));
            Assertions.assertEquals(exception.getMessage(), Constants.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("---GET ALL BOOKING BY OWNER TESTS---")
    class GetAllBookingsByOwnerTest {
        @Test
        void getAllBookingsByOwnerId() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
            when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));
            List<BookingDtoResponse> bookings = bookingService.getAllBookingsByOwnerId(owner.getId(), State.ALL, 0, 2);

            groupAssertChecking(bookings.get(0), booking);
        }

        @Test
        void getAllFutureBookingsByOwnerId() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
            when(bookingRepository.findALLByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                    anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));
            List<BookingDtoResponse> bookings = bookingService.getAllBookingsByOwnerId(owner.getId(), State.FUTURE, 0, 2);

            groupAssertChecking(bookings.get(0), booking);
        }

        @Test
        void getAllPastBookingsByOwnerId() {
            booking.setStart(LocalDateTime.now().minusSeconds(2));
            booking.setEnd(LocalDateTime.now().minusSeconds(1));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
            when(bookingRepository.findALLByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                    anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));
            List<BookingDtoResponse> bookings = bookingService.getAllBookingsByOwnerId(owner.getId(), State.PAST, 0, 2);

            groupAssertChecking(bookings.get(0), booking);
        }

        @Test
        void getAllCurrentBookingsByOwnerId() {
            booking.setStart(LocalDateTime.now().minusSeconds(1));
            booking.setEnd(LocalDateTime.now().plusSeconds(1));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
            when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                    anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));
            List<BookingDtoResponse> bookings = bookingService.getAllBookingsByOwnerId(owner.getId(), State.CURRENT, 0, 2);

            groupAssertChecking(bookings.get(0), booking);
        }

        @Test
        void getAllWaitingBookingsByOwnerId() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
            when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                    anyLong(), any(Status.class), any(Pageable.class))).thenReturn(List.of(booking));
            List<BookingDtoResponse> bookings = bookingService.getAllBookingsByOwnerId(owner.getId(), State.WAITING, 0, 2);

            groupAssertChecking(bookings.get(0), booking);
        }

        @Test
        void getAllRejectedBookingsByOwnerId() {
            booking.setStatus(Status.REJECTED);
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
            when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                    anyLong(), any(Status.class), any(Pageable.class))).thenReturn(List.of(booking));
            List<BookingDtoResponse> bookings = bookingService.getAllBookingsByOwnerId(owner.getId(), State.REJECTED, 0, 2);

            groupAssertChecking(bookings.get(0), booking);
        }

        @Test
        void getAllBookingsByOwnerIdWhenUserNotFound() {
            NotFoundException exception = assertThrows(
                    NotFoundException.class, () -> bookingService.getAllBookingsByOwnerId(booker.getId(), State.WAITING, 0, 2));
            Assertions.assertEquals(exception.getMessage(), Constants.USER_NOT_FOUND);
        }
    }

    private void groupAssertChecking(BookingDtoResponse bookingDtoResponse, Booking booking) {
        assertNotNull(bookingDtoResponse);
        Assertions.assertEquals(bookingDtoResponse.getId(), booking.getId());
        Assertions.assertEquals(bookingDtoResponse.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingDtoResponse.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingDtoResponse.getStart(), booking.getStart());
        Assertions.assertEquals(bookingDtoResponse.getEnd(), booking.getEnd());
        Assertions.assertEquals(bookingDtoResponse.getStatus(), booking.getStatus());
    }
}