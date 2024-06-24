package ru.practicum.shareit.booking.service;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toBooking(BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse toBookingDtoResponse(Booking booking);

    List<BookingDtoResponse> toBookingDtoResponseList(List<Booking> bookings);
}