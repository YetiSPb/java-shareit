package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    @Test
    void createSuccessfulWhenValid() throws IOException {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("It is perfectly made for testing")
                .available(true)
                .build();

        JsonContent<ItemDto> json = jacksonTester.write(dto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Item");
        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo("It is perfectly made for testing");
        assertThat(json).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }
}