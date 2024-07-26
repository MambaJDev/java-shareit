package ru.practicum.gateway.booking;

import java.time.LocalDateTime;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.gateway.booking.dto.BookingRequestDto;
import ru.practicum.gateway.booking.dto.BookingState;
import ru.practicum.gateway.constant.Constants;
import ru.practicum.gateway.error.BadRequestException;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class GatewayBookingController {

    @Autowired
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(Constants.HEADER_USER_ID) long userId,
                                           @RequestBody @Valid BookingRequestDto bookingRequestDto) {
        LocalDateTime startBooking = bookingRequestDto.getStart();
        LocalDateTime endBooking = bookingRequestDto.getEnd();
        if (endBooking.isBefore(startBooking) || endBooking.equals(startBooking)
                || startBooking.isBefore(LocalDateTime.now())) {
            log.error("Время бронирования некорректно");
            throw new BadRequestException(Constants.WRONG_BOOKING_DATE);
        }
        return bookingClient.bookItem(userId, bookingRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUser(@RequestHeader(Constants.HEADER_USER_ID) long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException(Constants.UNSUPPORTED_STATUS));
        return bookingClient.getBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(Constants.HEADER_USER_ID) long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException(Constants.UNSUPPORTED_STATUS));
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(Constants.HEADER_USER_ID) long userId,
                                             @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam Boolean approved) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }
}