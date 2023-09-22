package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.debug("Получен запрос GET на получение всех пользователей");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable(required = false) long id) {
        log.debug("Получен запрос GET на получение пользователя по id {}", id);
        return userService.findById(id);
    }

    @PostMapping()
    public UserDto saveUser(@RequestBody UserDto userDto) {
        log.debug("Получен запрос POST на создание пользователя {}", userDto.toString());
        return userService.saveUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto partialUpdateUser(@RequestBody UserDto userDto,
                                     @PathVariable(required = false) long id) {
        log.debug("Получен запрос PATCH на обновление пользователя по id {}", id);
        return userService.partialUpdateUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable(required = false) long id) {
        log.debug("Получен запрос DELETE для пользователя по id {}", id);
        userService.deleteUser(id);
    }
}
