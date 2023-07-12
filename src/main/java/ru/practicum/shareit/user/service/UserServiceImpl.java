package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        return userRepository.save(user);
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
        if (userToPatch.getEmail().equals(user.getEmail())) {
            return UserMapper.mapToUserDto(userToPatch);
        }
        return userRepository.partialUpdateUser(user, userToPatch);
    }

    @Override
    public void deleteUser(long id) {
        User user = userRepository.findById(id);
        userRepository.deleteUser(user);
    }
}
