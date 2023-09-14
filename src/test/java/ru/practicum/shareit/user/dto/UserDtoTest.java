package ru.practicum.shareit.user.dto;

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
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void createSuccessfulWhenValid() throws IOException {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("User")
                .email("valid@test.ru")
                .build();

        JsonContent<UserDto> json = jacksonTester.write(dto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("User");
        assertThat(json).extractingJsonPathStringValue("$.email")
                .isEqualTo("valid@test.ru");
    }

    @Test
    void createFailWhenBlankName() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("")
                .email("valid@test.ru")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void createFailWhenBlankEmail() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("User")
                .email("")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void createFailWhenIncorrectEmail() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("User")
                .email("incorrect.ru")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}