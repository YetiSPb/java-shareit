package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataConflictException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public List<User> findAll() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public User save(User user) {
        checkDuplicateEmail(user);
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(long id) {
        User user = users.get(id);

        if (user == null) {
            throw new DataNotFoundException("Пользователя с таким id нет в базе");
        }

        return user;
    }

    @Override
    public UserDto partialUpdateUser(User user, User userToPatch) {
        patchUser(userToPatch, user);
        return UserMapper.mapToUserDto(userToPatch);
    }

    @Override
    public void deleteUser(User user) {
        users.remove(user.getId());
    }

    private void patchUser(User userPatched, User user) {
        if (user.getName() != null) {
            userPatched.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (!userPatched.getEmail().equals(user.getEmail())) {
                checkDuplicateEmail(user);
            }
            userPatched.setEmail(user.getEmail());
        }
    }

    private void checkDuplicateEmail(User user) {
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                throw new DataConflictException("Пользователь с таким email уже есть в базе");
            }
        }
    }

    @Override
    public void checkUserId(Long userId) {
        findById(userId);
    }
}
