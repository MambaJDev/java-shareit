package ru.practicum.gateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.gateway.booking.dto.BookingRequestDto;
import ru.practicum.gateway.constant.Constants;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GatewayBookingController.class)
class GatewayBookingControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingClient bookingClient;

    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        bookingRequestDto = new BookingRequestDto()
                .setItemId(1L)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void saveBookingWhenFieldIsNull() throws Exception {
        bookingRequestDto.setItemId(null);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value(containsString("must not be null")));
    }

    @Test
    void saveBookingWhenEndIsBeforeStart() throws Exception {
        bookingRequestDto.setEnd(LocalDateTime.now().plusHours(1));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("Wrong booking date"));
    }

    @Test
    void saveBookingWhenEndIsEqualStart() throws Exception {
        bookingRequestDto.setStart(LocalDateTime.of(2024, 7, 14, 13, 0));
        bookingRequestDto.setEnd(LocalDateTime.of(2024, 7, 14, 13, 0));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("Wrong booking date"));
    }

    @Test
    void saveBookingWhenStartBeforeNow() throws Exception {
        bookingRequestDto.setStart(LocalDateTime.now().minusSeconds(1));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("Wrong booking date"));
    }

    @Test
    void getAllBookingsByOwnerIdWhenWrongState() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .param("state", "GIVEN")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.error").value("Unknown state: UNSUPPORTED_STATUS"));
    }

    @Test
    void getAllBookingsByOwnerIdWhenWrongParameters() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .param("from", "-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value(containsString("must be greater than or equal to 0")));
    }

    @Test
    void getAllBookingsByUserIdWhenWrongState() throws Exception {
        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .param("state", "GIVEN")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.error").value("Unknown state: UNSUPPORTED_STATUS"));
    }

    @Test
    void getAllBookingsByUserIdWhenWrongParameters() throws Exception {
        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .param("from", "-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value(containsString("must be greater than or equal to 0")));
    }
}