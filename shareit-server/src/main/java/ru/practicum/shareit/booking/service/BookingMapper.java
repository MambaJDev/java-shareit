package ru.practicum.shareit.booking.service;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(ignore = true, target = "id")
    Booking toBooking(BookingDtoRequest bookingDtoRequest, User booker, Item item, Status status);

    BookingDtoResponse toBookingDtoResponse(Booking booking);

    List<BookingDtoResponse> toBookingDtoResponseList(List<Booking> bookings);
}