package ru.practicum.shareit.booking.service;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.State;

public interface BookingService {
    BookingDtoResponse save(Long userId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse approveBooking(Long userId, Long bookingId, Boolean approved);

    BookingDtoResponse getBookingById(Long userId, Long bookingId);

    List<BookingDtoResponse> getAllBookingsByUserId(Long userId, State state, Integer from, Integer size);

    List<BookingDtoResponse> getAllBookingsByOwnerId(Long ownerId, State state, Integer from, Integer size);
}