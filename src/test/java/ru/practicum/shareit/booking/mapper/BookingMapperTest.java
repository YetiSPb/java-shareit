package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookItemDto;
import ru.practicum.shareit.booking.dto.BookerAndItemDto;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {

    private BookingDto dto;
    private Booking booking;
    private User requester;
    private Item item = new Item();

    private final LocalDateTime date =
            LocalDateTime.of(2023, Month.AUGUST, 4, 15, 16, 1);

    @BeforeEach
    void setUp() {
        long ownerId = 1L;
        long requesterId = 2L;
        BookerDto bookerDto = new BookerDto(2L);

        BookItemDto bookItemDto = new BookItemDto(1L, "Test Item");

        User owner = User.builder()
                .id(ownerId)
                .name("TestRob")
                .email("test2@test.ru")
                .items(Set.of(item))
                .build();

        requester = User.builder()
                .id(requesterId)
                .name("TestBob")
                .email("test@test.ru")
                .items(Set.of(new Item()))
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Perfect Test Item Ever")
                .user(owner)
                .available(true)
                .comments(new ArrayList<>())
                .itemRequest(new ItemRequest())
                .build();

        booking = Booking.builder()
                .id(1L)
                .booker(requester)
                .item(item)
                .start(date)
                .end(date.plusHours(2))
                .status(Status.WAITING)
                .build();

        dto = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(date)
                .end(date.plusHours(2))
                .booker(bookerDto)
                .item(bookItemDto)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void mapToBooking() {
        Booking actualBooking = BookingMapper.mapToBooking(dto, requester, item, Status.WAITING);

        assertEquals(actualBooking, booking);
    }

    @Test
    void mapToBookingDto() {
        BookingDto actualDto = BookingMapper.mapToBookingDto(booking);

        assertThat(actualDto.getId(), is(dto.getId()));
        assertThat(actualDto.getStart(), is(dto.getStart()));
        assertThat(actualDto.getEnd(), is(dto.getEnd()));
        assertThat(actualDto.getBooker(), is(dto.getBooker()));
        assertThat(actualDto.getStatus(), is(dto.getStatus()));
    }

    @Test
    void mapToBookerAndItemDto() {
        BookerAndItemDto actualDto = BookingMapper.mapToBookerAndItemDto(booking);

        assertThat(actualDto.getBookerId(), is(requester.getId()));
    }
}