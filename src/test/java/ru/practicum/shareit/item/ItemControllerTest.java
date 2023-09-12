package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUserDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService service;

    private ItemDto dto;

    private long userId;


    @BeforeEach
    void setUp() {
        dto = ItemDto.builder()
                .name("Test Item")
                .description("Perfect Test Item Ever")
                .available(true)
                .build();

        userId = 1L;
        User.builder()
                .id(userId)
                .items(Set.of(Item.builder().id(dto.getId()).build()))
                .build();
    }

    @Test
    void contextLoad() {
        assertThat(service).isNotNull();
    }

    @Test
    void testSaveItemOkWhenValidWithoutRequestId() throws Exception {
        when(service.saveItem(dto, userId))
                .thenReturn(dto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.available", is(dto.getAvailable())));
    }

    @Test
    void testSaveItemOkWhenValidWithRequestId() throws Exception {
        dto.setRequestId(1L);
        when(service.saveItem(dto, userId, dto.getRequestId()))
                .thenReturn(dto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.available", is(dto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(dto.getRequestId()), Long.class));
    }

    @Test
    void testSaveItem400WhenNameIsBlank() throws Exception {
        dto.setName(null);
        when(service.saveItem(dto, userId))
                .thenThrow(new ValidationException());

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testSaveItem404WhenUserNotFound() throws Exception {
        long wrongId = 29L;
        when(service.saveItem(dto, wrongId))
                .thenThrow(new DataNotFoundException());

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", wrongId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testPartialUpdateItemOkWhenValid() throws Exception {
        dto.setName("New name");
        dto.setDescription("New Description");
        dto.setAvailable(false);
        Map<String, Object> updates = new HashMap<>();
        updates.put("available", dto.getAvailable());
        updates.put("name", dto.getName());
        updates.put("description", dto.getDescription());

        when(service.partialUpdateItem(updates, dto.getId(), userId))
                .thenReturn(dto);

        mvc.perform(patch("/items/0")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(updates))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.available", is(dto.getAvailable())));
    }

    @Test
    void testPartialUpdateItem404WhenWrongItemId() throws Exception {
        dto.setName("New name");
        dto.setDescription("New Description");
        dto.setAvailable(false);
        Map<String, Object> updates = new HashMap<>();
        updates.put("available", dto.getAvailable());
        updates.put("name", dto.getName());
        updates.put("description", dto.getDescription());

        when(service.partialUpdateItem(updates, 23L, userId))
                .thenThrow(new DataNotFoundException());

        mvc.perform(patch("/items/23")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(updates))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testFindByIdOkWhenValid() throws Exception {
        ItemForUserDto item = ItemForUserDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();

        when(service.findById(dto.getId(), userId))
                .thenReturn(item);

        mvc.perform(get("/items/0")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.available", is(dto.getAvailable())));
    }

    @Test
    void testFindAllItemsOkWhenValid() throws Exception {
        ItemForUserDto item = ItemForUserDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
        Pageable page = PageRequest.of(0, 20);

        when(service.findAllItems(userId, page))
                .thenReturn(List.of(item));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(dto.getName())))
                .andExpect(jsonPath("$[0].description", is(dto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(dto.getAvailable())));
    }

    @Test
    void testFindAllItems500WhenWrongPageParams() throws Exception {
        Pageable page = PageRequest.of(0, 20);

        when(service.findAllItems(userId, page))
                .thenThrow(new IllegalArgumentException());

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testSearchItemsOkWhenValid() throws Exception {
        Pageable page = PageRequest.of(0, 20);
        String text = "perfect";

        when(service.searchItems(text, userId, page))
                .thenReturn(List.of(dto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "Perfect")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(dto.getName())))
                .andExpect(jsonPath("$[0].description", is(dto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(dto.getAvailable())));
    }

    @Test
    void testSearchItemsEmptyListWithBlankSearchText() throws Exception {
        Pageable page = PageRequest.of(0, 20);
        String text = "";

        when(service.searchItems(text, userId, page))
                .thenReturn(List.of(dto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testAddCommentOkWhenValid() throws Exception {
        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("This item is amazing")
                .authorName("Peter")
                .created(LocalDateTime.of(2023, Month.AUGUST, 4, 15, 16, 1))
                .build();
        User user1 = User.builder()
                .id(2L)
                .items(Set.of(new Item()))
                .name("Peter")
                .email("some@mail.ru")
                .build();

        when(service.addComment(user1.getId(), dto.getId(), comment))
                .thenReturn(comment);

        mvc.perform(post("/items/0/comment")
                        .header("X-Sharer-User-Id", user1.getId())
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(user1.getName())))
                .andExpect(jsonPath("$.created").value(comment.getCreated().toString()));
    }
}