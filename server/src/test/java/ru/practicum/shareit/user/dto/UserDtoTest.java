package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> jacksonTester;

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
}