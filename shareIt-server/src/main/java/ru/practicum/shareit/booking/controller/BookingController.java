package ru.practicum.shareit.booking.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.Constants;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse saveBooking(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                          @RequestBody BookingDtoRequest bookingDtoRequest) {
        log.info("Post-запрос saveBooking");
        return bookingService.save(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approveBooking(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        log.info("Patch-запрос approveBooking");
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    BookingDtoResponse getBookingById(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                      @PathVariable Long bookingId) {
        log.info("Get-запрос getBookingById");
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    List<BookingDtoResponse> getAllBookingsByUserId(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                                    @RequestParam State state,
                                                    @RequestParam Integer from,
                                                    @RequestParam Integer size) {
        log.info("Get-запрос getAllBookingsByUserId");
        return bookingService.getAllBookingsByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    List<BookingDtoResponse> getAllBookingsByOwnerId(@RequestHeader(Constants.HEADER_USER_ID) Long ownerId,
                                                     @RequestParam State state,
                                                     @RequestParam Integer from,
                                                     @RequestParam Integer size) {
        log.info("Get-запрос getAllBookingsByOwnerId");
        return bookingService.getAllBookingsByOwnerId(ownerId, state, from, size);
    }
}