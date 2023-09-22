package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemDto;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.repository.OffsetLimitPageable;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService service;

    private BookingDto dto;
    private long userId;
    private BookItemDto item;

    @BeforeEach
    void setUp() { // делаю конкретную дату, не LocalDateTime.now() потому что миллисекунды заваливают тесты
        LocalDateTime time = LocalDateTime.of(2023, Month.AUGUST, 10, 9, 10, 1);
        item = new BookItemDto();
        item.setId(1L);

        dto = BookingDto.builder()
                .itemId(1L)
                .start(time.plusMonths(2))
                .end(time.plusMonths(3))
                .build();

        userId = 1L;
    }

    @Test
    void contextLoad() {
        assertThat(service).isNotNull();
    }

    @Test
    void testAddBookingOkWhenValid() throws Exception {
        when(service.addBooking(userId, dto))
                .thenReturn(dto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.start").value(dto.getStart().toString()))
                .andExpect(jsonPath("$.end").value((dto.getEnd().toString())));
    }

    @Test
    void testAddBooking400WhenItemIdIsNull() throws Exception {
        dto.setItemId(null);
        when(service.addBooking(userId, dto))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testAddBooking400WhenStartTimeEqualsEndTime() throws Exception {
        dto.setEnd(dto.getStart());
        when(service.addBooking(userId, dto))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testApproveBookingOkWhenValid() throws Exception {
        BookerDto booker = new BookerDto();
        booker.setId(1L);
        dto.setBooker(booker);
        dto.setStatus(Status.APPROVED);
        User owner = new User();
        owner.setId(2L);

        when(service.approveBooking(owner.getId(), dto.getId(), true))
                .thenReturn(dto);

        mvc.perform(patch("/bookings/0")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.status").value(dto.getStatus().toString()));
    }

    @Test
    void testApproveBooking404WhenWrongBookingId() throws Exception {
        when(service.approveBooking(userId, 1, true))
                .thenThrow(new DataNotFoundException());

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testApproveBooking404WhenWrongUserId() throws Exception {
        long id = 3L;
        when(service.approveBooking(id, dto.getId(), true))
                .thenThrow(new DataNotFoundException());

        mvc.perform(patch("/bookings/0")
                        .header("X-Sharer-User-Id", id)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testFindByIdOkWhenValid() throws Exception {
        when(service.findById(userId, dto.getId()))
                .thenReturn(dto);

        mvc.perform(get("/bookings/0")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.start").value(dto.getStart().toString()))
                .andExpect(jsonPath("$.end").value((dto.getEnd().toString())));
    }

    @Test
    void testFindById404WhenWrongBookingId() throws Exception {
        long wrongId = 20L;
        when(service.findById(userId, wrongId))
                .thenThrow(new DataNotFoundException());

        mvc.perform(get("/bookings/20")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testFindAllBookingsByUserOkWhenValid() throws Exception {
        BookerDto booker = new BookerDto();
        booker.setId(1L);
        dto.setBooker(booker);
        dto.setStatus(Status.APPROVED);
        Pageable page = OffsetLimitPageable.of(0, 20);

        when(service.findAllBookingsByUser(userId, BookingState.ALL, page))
                .thenReturn(List.of(dto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].start").value(dto.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value((dto.getEnd().toString())));
    }

    @Test
    void testFindAllBookingsByUser400WhenUnsupportedState() throws Exception {
        BookerDto booker = new BookerDto();
        booker.setId(1L);
        dto.setBooker(booker);
        dto.setStatus(Status.APPROVED);
        Pageable page = OffsetLimitPageable.of(0, 20);
        // нельзя выбрать несуществующий BookingState, поэтому указала конкретный
        when(service.findAllBookingsByUser(userId, BookingState.REJECTED, page))
                .thenThrow(new UnsupportedStatusException("Unknown state"));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "UNSUPPORTED")
                        .param("from", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testFindAllBookingsByOwnerOkWhenValid() throws Exception {
        BookerDto booker = new BookerDto();
        booker.setId(1L);
        dto.setBooker(booker);
        dto.setStatus(Status.APPROVED);
        User owner = new User();
        Item item1 = new Item();
        item1.setId(1L);
        owner.setId(2L);
        owner.setItems(Set.of(item1));

        Pageable page = OffsetLimitPageable.of(0, 20);

        when(service.findAllBookingsByOwner(owner.getId(), BookingState.ALL, page))
                .thenReturn(List.of(dto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].start").value(dto.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value((dto.getEnd().toString())));
    }
}