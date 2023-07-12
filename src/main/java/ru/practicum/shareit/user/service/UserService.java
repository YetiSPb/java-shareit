package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto saveUser(UserDto userDto);

    UserDto findById(long id);

    UserDto partialUpdateUser(UserDto userDto, long id);

    void deleteUser(long id);
}
