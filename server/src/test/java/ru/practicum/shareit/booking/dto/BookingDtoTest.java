package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> jacksonTester;

    private final LocalDateTime now =
            LocalDateTime.of(2023, Month.SEPTEMBER, 4, 15, 16, 1);

    @Test
    void testNotNullItemIdSuccessful() throws IOException {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(now)
                .end(now.plusMonths(1))
                .build();

        JsonContent<BookingDto> json = jacksonTester.write(dto);

        assertThat(json).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo("2023-09-04T15:16:01");
        assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo("2023-10-04T15:16:01");
    }
}