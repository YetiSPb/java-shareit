package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    private long id;
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный email")
    private String email;
}
