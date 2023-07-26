package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto save(UserDto userDto);

    UserDto findById(long id);

    UserDto updateUser(UserDto userDto, long id);

    void deleteUser(long id);

}
