package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    @Test
    void createSuccessfulWhenValid() throws IOException {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Some test description")
                .build();

        JsonContent<ItemRequestDto> json = jacksonTester.write(dto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo("Some test description");
    }
}