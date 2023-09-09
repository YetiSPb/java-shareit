package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers(Pageable page) {
        return userRepository.findAll(page).stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserDto save(UserDto userDto) {
        User user = userMapper.toModel(userDto);
        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public UserDto findById(long id) {
        User user = userRepository.findById(id).orElseThrow(DataNotFoundException::new);
        return userMapper.toDTO(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        User user = userRepository.findById(id).orElseThrow(DataNotFoundException::new);

        User userUpdate = userMapper.toModel(userDto);

        if (userUpdate.getName() != null) {
            user.setName(userUpdate.getName());
        }

        if (userUpdate.getEmail() != null) {
            if (!user.getEmail().equals(userUpdate.getEmail())) {
                userRepository.checkDuplicateEmail(userUpdate.getEmail());
            }
            user.setEmail(userUpdate.getEmail());
        }

        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

}
