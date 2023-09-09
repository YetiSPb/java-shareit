package ru.practicum.shareit.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers(Pageable page);

    UserDto save(UserDto userDto);

    UserDto findById(long id);

    UserDto updateUser(UserDto userDto, long id);

    void deleteUser(long id);

}
