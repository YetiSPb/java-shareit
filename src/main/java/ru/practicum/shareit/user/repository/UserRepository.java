package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    List<UserDto> findAll();

    UserDto save(User user);

    User findById(long id);

    UserDto partialUpdateUser(User user, User userToPatch);

    void deleteUser(User user);
}
