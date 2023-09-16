package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookerAndItemDtoTest {

    @Autowired
    private JacksonTester<BookerAndItemDto> jacksonTester;

    @Test
    void testNotNullItemIdSuccessful() throws IOException {
        BookerAndItemDto dto = BookerAndItemDto.builder()
                .id(1L)
                .bookerId(1L)
                .build();

        JsonContent<BookerAndItemDto> json = jacksonTester.write(dto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }
}