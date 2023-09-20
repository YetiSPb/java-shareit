package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookerDtoTest {

    @Autowired
    private JacksonTester<BookerDto> jacksonTester;

    @Test
    void testNotNullItemIdSuccessful() throws IOException {
        BookerDto dto = new BookerDto(1L);

        JsonContent<BookerDto> json = jacksonTester.write(dto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }
}