package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoResponse save(Long userId, BookingDtoRequest bookingDtoRequest) {
        LocalDateTime startBooking = bookingDtoRequest.getStart();
        LocalDateTime endBooking = bookingDtoRequest.getEnd();
        if (endBooking.isBefore(startBooking) || endBooking.equals(startBooking)
                || startBooking.isBefore(LocalDateTime.now())) {
            log.error("Время бронирования некорректно");
            throw new BadRequestException(Constants.WRONG_BOOKING_DATE);
        }
        Item item = itemRepository.findById(bookingDtoRequest.getItemId()).orElseThrow(() -> new NotFoundException(Constants.ITEM_NOT_FOUND));
        if (!item.getAvailable()) {
            log.error("Item недоступен для бронирования");
            throw new BadRequestException(Constants.NOT_AVAILABLE_ITEM);
        }
        if (item.getOwner().getId().equals(userId)) {
            log.error("Бронирование не доступно для Owner");
            throw new NotFoundException(Constants.OWNER_CAN_NOT_BOOK);
        }
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
        Booking booking = bookingMapper.toBooking(bookingDtoRequest);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        return bookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(Constants.BOOKING_NOT_FOUND));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException(Constants.USER_CAN_NOT_GET_ITEM);
        }
        return bookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(Constants.BOOKING_NOT_FOUND));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(Constants.USER_NOT_OWNER);
        }
        if (approved && booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException(Constants.APPROVED_ITEM);
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return bookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public List<BookingDtoResponse> getAllBookingsByUserId(Long bookerId, State state) {
        userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
        switch (state) {
            case ALL: {
                return bookingMapper.toBookingDtoResponseList(bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId));
            }
            case FUTURE: {
                return bookingMapper.toBookingDtoResponseList(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, LocalDateTime.now()));
            }
            case WAITING: {
                return bookingMapper.toBookingDtoResponseList(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING));
            }
            case REJECTED: {
                return bookingMapper.toBookingDtoResponseList(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED));
            }
            case CURRENT: {
                return bookingMapper.toBookingDtoResponseList(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(bookerId, LocalDateTime.now(), LocalDateTime.now()));
            }
            case PAST: {
                return bookingMapper.toBookingDtoResponseList(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(bookerId, LocalDateTime.now()));
            }
        }
        return null;
    }

    @Override
    public List<BookingDtoResponse> getAllBookingsByOwnerId(Long ownerId, State state) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
        switch (state) {
            case ALL: {
                return bookingMapper.toBookingDtoResponseList(
                        bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId));
            }
            case WAITING: {
                return bookingMapper.toBookingDtoResponseList(
                        bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING));
            }
            case REJECTED: {
                return bookingMapper.toBookingDtoResponseList(
                        bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED));
            }
            case CURRENT: {
                return bookingMapper.toBookingDtoResponseList(
                        bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now()));
            }
            case FUTURE: {
                return bookingMapper.toBookingDtoResponseList(
                        bookingRepository.findALLByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, LocalDateTime.now()));
            }
            case PAST: {
                return bookingMapper.toBookingDtoResponseList(
                        bookingRepository.findALLByItemOwnerIdAndEndIsBeforeOrderByStartDesc(ownerId, LocalDateTime.now()));
            }
        }
        return null;
    }
}