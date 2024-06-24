package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDtoResponse save(Long userId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse approveBooking(Long userId, Long bookingId, Boolean approved);

    BookingDtoResponse getBookingById(Long userId, Long bookingId);

    List<BookingDtoResponse> getAllBookingsByUserId(Long userId, State state);

    List<BookingDtoResponse> getAllBookingsByOwnerId(Long ownerId, State state);
}