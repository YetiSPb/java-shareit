package ru.practicum.shareit.booking.dto;

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
class BookingDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final LocalDateTime now =
            LocalDateTime.of(2023, Month.SEPTEMBER, 4, 15, 16, 1);
    @Autowired
    private JacksonTester<BookingDto> jacksonTester;

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

    @Test
    void testNullItemIdFail() {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(now)
                .end(now.plusMonths(1))
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void testStartInPastFail() {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(now.minusMonths(2))
                .end(now.plusMonths(1))
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void testNullStartFail() {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .end(now.plusMonths(1))
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void testNullEndFail() {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(now.plusMonths(1))
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void testEndInPastFail() {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(now.plusMonths(1))
                .end(now.minusYears(1))
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}