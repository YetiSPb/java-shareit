package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto findById(long id) {
        User user = userRepository.findById(id);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto partialUpdateUser(UserDto userDto, long id) {
        User userToPatch = userRepository.findById(id);
        User user = UserMapper.mapToUser(userDto);
        return userRepository.partialUpdateUser(user, userToPatch);
    }

    @Override
    public void deleteUser(long id) {
        User user = userRepository.findById(id);
        userRepository.deleteUser(user);
    }

}
