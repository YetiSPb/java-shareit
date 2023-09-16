package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto findById(long id) {
        User user = userRepository.findById(id);
        checkIfUserExists(user);
        return UserMapper.mapToUserDto(user);

    }

    @Override
    public UserDto partialUpdateUser(UserDto userDto, long id) {
        User userToPatch = userRepository.findById(id);
        checkIfUserExists(userToPatch);
        User user = UserMapper.mapToUser(userDto);
        if (userToPatch.getEmail().equals(user.getEmail())) {
            return UserMapper.mapToUserDto(userToPatch);
        }
        return UserMapper.mapToUserDto(userRepository.save(patchUser(userToPatch, user)));
    }

    @Override
    public void deleteUser(long id) {
        User user = userRepository.findById(id);
        checkIfUserExists(user);
        userRepository.delete(user);
    }

    private User patchUser(User userPatched, User user) {
        if (user.getName() != null) {
            userPatched.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userPatched.setEmail(user.getEmail());
        }
        return userPatched;
    }

    private void checkIfUserExists(User user) {
        if (user == null) {
            throw new DataNotFoundException("Пользователя с таким id нет в базе");
        }
    }
}
