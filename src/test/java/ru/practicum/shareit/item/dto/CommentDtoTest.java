package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final LocalDateTime now =
            LocalDateTime.of(2023, Month.SEPTEMBER, 4, 15, 16, 1);
    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

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

    @Test
    void testCreateFailWhenNullText() {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .authorName("Some great author has written this")
                .created(now)
                .build();

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void testCreateFailWhenBlankText() {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("")
                .authorName("Some great author has written this")
                .created(now)
                .build();

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}