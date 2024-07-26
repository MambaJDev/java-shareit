package ru.practicum.shareit.booking.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.hamcrest.Matchers;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.Constants;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    private final EasyRandom generator = new EasyRandom();

    @Test
    void saveBooking() throws Exception {
        BookingDtoRequest bookingDtoRequest = generator.nextObject(BookingDtoRequest.class);
        BookingDtoResponse bookingDtoResponse = generator.nextObject(BookingDtoResponse.class);
        when(bookingService.save(anyLong(), any(BookingDtoRequest.class))).thenReturn(bookingDtoResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(bookingDtoResponse.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.id", Matchers.is(bookingDtoResponse.getBooker().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.id", Matchers.is(bookingDtoResponse.getItem().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name", Matchers.is(bookingDtoResponse.getItem().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(bookingDtoResponse.getStatus().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start", Matchers.is(bookingDtoResponse.getStart().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end", Matchers.is(bookingDtoResponse.getEnd().toString())));
    }

    @Test
    void approveBooking() throws Exception {
        BookingDtoResponse bookingDtoResponse = generator.nextObject(BookingDtoResponse.class);
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoResponse);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .queryParam("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(bookingDtoResponse.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.id", Matchers.is(bookingDtoResponse.getBooker().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.id", Matchers.is(bookingDtoResponse.getItem().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name", Matchers.is(bookingDtoResponse.getItem().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(bookingDtoResponse.getStatus().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start", Matchers.is(bookingDtoResponse.getStart().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end", Matchers.is(bookingDtoResponse.getEnd().toString())));
    }

    @Test
    void getBookingById() throws Exception {
        BookingDtoResponse bookingDtoResponse = generator.nextObject(BookingDtoResponse.class);
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDtoResponse);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(bookingDtoResponse.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.id", Matchers.is(bookingDtoResponse.getBooker().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.id", Matchers.is(bookingDtoResponse.getItem().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name", Matchers.is(bookingDtoResponse.getItem().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(bookingDtoResponse.getStatus().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start", Matchers.is(bookingDtoResponse.getStart().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end", Matchers.is(bookingDtoResponse.getEnd().toString())));
    }

    @Test
    void getAllBookingsByUserId() throws Exception {
        BookingDtoResponse bookingDtoResponse = generator.nextObject(BookingDtoResponse.class);
        List<BookingDtoResponse> bookingDtoResponses = List.of(bookingDtoResponse);
        when(bookingService.getAllBookingsByUserId(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(bookingDtoResponses);

        mvc.perform(get("/bookings")
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['id']", Matchers.is(bookingDtoResponses.get(0).getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['booker']['id']", Matchers.is((bookingDtoResponses.get(0).getBooker().getId()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['item']['id']", Matchers.is((bookingDtoResponses.get(0).getItem().getId()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['item']['name']", Matchers.is((bookingDtoResponses.get(0).getItem().getName()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['status']", Matchers.is((bookingDtoResponses.get(0).getStatus().toString()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['start']", Matchers.is((bookingDtoResponses.get(0).getStart().toString()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['end']", Matchers.is((bookingDtoResponses.get(0).getEnd().toString()))));
    }

    @Test
    void getAllBookingsByOwnerId() throws Exception {

        BookingDtoResponse bookingDtoResponse = generator.nextObject(BookingDtoResponse.class);
        List<BookingDtoResponse> bookingDtoResponses = List.of(bookingDtoResponse);
        when(bookingService.getAllBookingsByOwnerId(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(bookingDtoResponses);

        mvc.perform(get("/bookings/owner/")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['id']", Matchers.is(bookingDtoResponses.get(0).getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['booker']['id']", Matchers.is((bookingDtoResponses.get(0).getBooker().getId()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['item']['id']", Matchers.is((bookingDtoResponses.get(0).getItem().getId()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['item']['name']", Matchers.is((bookingDtoResponses.get(0).getItem().getName()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['status']", Matchers.is((bookingDtoResponses.get(0).getStatus().toString()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['start']", Matchers.is((bookingDtoResponses.get(0).getStart().toString()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['end']", Matchers.is((bookingDtoResponses.get(0).getEnd().toString()))));
    }
}