package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    private UserDto dto;
    private User user;
    private long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;

        dto = UserDto.builder()
                .id(userId)
                .name("TestBob")
                .email("test@test.ru")
                .build();

        user = User.builder()
                .id(userId)
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    @Test
    void testFindAllUsersOk() {
        when(repository.findAll())
                .thenReturn(List.of(user));

        List<UserDto> users = service.findAllUsers();
        UserDto actualUser = users.get(0);

        assertThat(actualUser.getId(), is(dto.getId()));
        assertThat(actualUser.getName(), is(dto.getName()));
        assertThat(actualUser.getEmail(), is(dto.getEmail()));
    }

    @Test
    void testSaveUserOk() {
        when(repository.save(user))
                .thenReturn(user);

        UserDto actualUser = service.saveUser(dto);

        assertThat(actualUser.getId(), is(dto.getId()));
        assertThat(actualUser.getName(), is(dto.getName()));
        assertThat(actualUser.getEmail(), is(dto.getEmail()));
    }

    @Test
    void testSaveUserFailWhenSameEmail() {
        User withSameEmail = User.builder()
                .id(userId)
                .name(dto.getName())
                .email(dto.getEmail())
                .build();

        when(repository.save(withSameEmail))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> service.saveUser(dto));
    }

    @Test
    void testFindByIdOk() {
        when(repository.findById(userId))
                .thenReturn(user);

        UserDto actualUser = service.findById(userId);

        assertThat(actualUser.getId(), is(dto.getId()));
        assertThat(actualUser.getName(), is(dto.getName()));
        assertThat(actualUser.getEmail(), is(dto.getEmail()));
    }

    @Test
    void testFindByIdFailWhenWrongId() {
        when(repository.findById(22L))
                .thenReturn(null);

        assertThrows(DataNotFoundException.class, () -> service.findById(22L));
    }

    @Test
    void testFindByIdWhenWrongIdNotFound() {
        long wrongId = 2L;

        when(repository.findById(wrongId))
                .thenThrow(new DataNotFoundException());

        assertThrows(DataNotFoundException.class, () -> service.findById(wrongId));
    }

    @Test
    void testPartialUpdateUserOk() {
        User user1 = User.builder()
                .id(userId)
                .name("New name")
                .email("new email")
                .build();
        dto.setEmail("new email");
        dto.setName("New name");

        when(repository.findById(userId)).thenReturn(user);
        when(repository.save(user1)).thenReturn(user1);

        UserDto actualUser = service.partialUpdateUser(dto, userId);

        assertThat(actualUser.getId(), is(dto.getId()));
        assertThat(actualUser.getName(), is(dto.getName()));
        assertThat(actualUser.getEmail(), is(dto.getEmail()));

        verify(repository, Mockito.times(1))
                .save(user1);
    }

    @Test
    void testDeleteUserOk() {
        when(repository.findById(userId)).thenReturn(user);
        service.deleteUser(userId);

        verify(repository, Mockito.times(1))
                .delete(user);
    }

    @Test
    void testDeleteUserNotFound() {
        long wrongId = 2L;
        when(repository.findById(wrongId)).thenThrow(new DataNotFoundException());

        assertThrows(DataNotFoundException.class, () -> service.deleteUser(wrongId));
    }
}