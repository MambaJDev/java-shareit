package ru.practicum.shareit.booking.dto;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoRequestTest {
    @Autowired
    private JacksonTester<BookingDtoRequest> tester;

    @Test
    void bookingDtoRequestCheckJsonBodyTest() throws IOException {
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest()
                .setItemId(1L)
                .setStart(LocalDateTime.now())
                .setEnd(LocalDateTime.now().plusMinutes(1));
        JsonContent<BookingDtoRequest> result = tester.write(bookingDtoRequest);

        assertThat(result).isNotNull().hasJsonPath("$.itemId");
        assertThat(result).isNotNull().hasJsonPath("start");
        assertThat(result).isNotNull().hasJsonPath("end");
    }
}