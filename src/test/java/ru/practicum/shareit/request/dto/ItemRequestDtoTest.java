package ru.practicum.shareit.request.dto;

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
class ItemRequestDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
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

    @Test
    void createFailWhenNullDescription() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description(null)
                .build();

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}