package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService service;

    private UserDto dto;

    private long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;

        dto = UserDto.builder()
                .id(userId)
                .name("TestBob")
                .email("test@test.ru")
                .build();

        User.builder()
                .id(userId)
                .items(Set.of(Item.builder().id(1L).build()))
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    @Test
    void contextLoad() {
        assertThat(service).isNotNull();
    }

    @Test
    void testFindAllUsersOkWhenValid() throws Exception {
        when(service.findAllUsers())
                .thenReturn(List.of(dto));

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(dto.getName())))
                .andExpect(jsonPath("$[0].email", is(dto.getEmail())));
    }

    @Test
    void testFindByIdOkWhenValid() throws Exception {
        when(service.findById(userId))
                .thenReturn(dto);

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.email", is(dto.getEmail())));
    }

    @Test
    void testFindById404WhenWrongId() throws Exception {
        long wrongId = 22L;

        when(service.findById(wrongId))
                .thenThrow(new DataNotFoundException());

        mvc.perform(get("/users/22")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testSaveUserOkWhenValid() throws Exception {
        when(service.saveUser(dto))
                .thenReturn(dto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.email", is(dto.getEmail())));
    }

    @Test
    void testSaveUser400WhenUserNameBlank() throws Exception {
        dto.setName("");

        when(service.saveUser(dto))
                .thenThrow(new ValidationException());

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testSaveUser400WhenUserEmailWrong() throws Exception {
        dto.setEmail("testtest.ru");

        when(service.saveUser(dto))
                .thenThrow(new ValidationException());

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testPartialUpdateUserOkWhenValid() throws Exception {
        dto.setName("New name");
        dto.setEmail("cool-new-email@test.ru");

        when(service.partialUpdateUser(dto, userId))
                .thenReturn(dto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.email", is(dto.getEmail())));
    }

    @Test
    void testDeleteUserOkWhenValid() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(service, Mockito.times(1))
                .deleteUser(userId);
    }
}