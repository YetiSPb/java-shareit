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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
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

    @Test
    void createFailWhenBlankName() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("")
                .description("It is perfectly made for testing")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void createFailWhenBlankDescription() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Test")
                .description("")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void createFailWhenDescriptionOver200Symbols() {
        String over200SymbolDescription = "This is a very very very very very very very very very very very long " +
                "description. The entire Earth has not seen a longer description for a test item in the history " +
                "of mankind, which calculates over 5000 years by some estimates.";

        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Test")
                .description(over200SymbolDescription)
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void createFailWhenAvailableNull() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("It is perfectly made for testing")
                .available(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}