package ru.practicum.shareit.item.dto;

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
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

    private final LocalDateTime now =
            LocalDateTime.of(2023, Month.SEPTEMBER, 4, 15, 16, 1);

    @Test
    void testCreateSuccessfulWhenValid() throws IOException {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("Some test text for a comment")
                .authorName("Some great author has written this")
                .created(now)
                .build();

        JsonContent<CommentDto> json = jacksonTester.write(dto);

        assertThat(json).extractingJsonPathStringValue("$.text")
                .isEqualTo("Some test text for a comment");
        assertThat(json).extractingJsonPathStringValue("$.authorName")
                .isEqualTo("Some great author has written this");
        assertThat(json).extractingJsonPathStringValue("$.created")
                .isEqualTo("2023-09-04T15:16:01");
    }
}