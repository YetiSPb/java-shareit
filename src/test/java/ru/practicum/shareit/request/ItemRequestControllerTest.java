package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService service;

    private ItemRequestDto dto;

    private long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;

        dto = ItemRequestDto.builder()
                .description("I want a perfect test item")
                .requesterId(userId)
                .created(LocalDateTime.of(2023, Month.AUGUST, 4, 15, 16, 1))
                .build();

        User.builder()
                .id(userId)
                .email("test@test.ru")
                .name("test Name")
                .items(Set.of(new Item()))
                .build();
    }

    @Test
    void contextLoad() {
        assertThat(service).isNotNull();
    }

    @Test
    void testAddItemRequestOkWhenValid() throws Exception {
        when(service.addItemRequest(userId, dto))
                .thenReturn(dto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.created").value((dto.getCreated().toString())));
    }

    @Test
    void testAddItemRequest400WhenItemRequestDescriptionNull() throws Exception {
        dto.setDescription(null);

        when(service.addItemRequest(userId, dto))
                .thenThrow(new ValidationException());

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testAddItemRequest404WhenWrongUserId() throws Exception {
        long wrongId = 22L;

        when(service.addItemRequest(wrongId, dto))
                .thenThrow(new DataNotFoundException());

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", wrongId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testFindAllOwnRequestsOkWhenValid() throws Exception {
        when(service.findAllOwnRequests(userId))
                .thenReturn(List.of(dto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(dto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(dto.getCreated().toString())));
    }

    @Test
    void testFindAllItemRequestsOkWhenValid() throws Exception {
        long newUserId = 2L;
        User.builder().id(newUserId).build();

        when(service.findAllItemRequests(newUserId, 0, 10))
                .thenReturn(List.of(dto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", newUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(dto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(dto.getCreated().toString())));
    }

    @Test
    void testFindAllItemRequests500WhenNegativePage() throws Exception {
        long newUserId = 2L;
        User.builder().id(newUserId).build();

        when(service.findAllItemRequests(newUserId, -1, 10))
                .thenThrow(new IllegalArgumentException());

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", newUserId)
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testFindByIdOkWhenValid() throws Exception {
        when(service.findById(userId, dto.getId()))
                .thenReturn(dto);

        mvc.perform(get("/requests/0")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.created", is(dto.getCreated().toString())));
    }

    @Test
    void testFindById404WhenRequestIdWrong() throws Exception {
        long wrongId = 22L;
        when(service.findById(userId, wrongId))
                .thenThrow(new DataNotFoundException());

        mvc.perform(get("/requests/22")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}