package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookerAndItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUserDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemMapperTest {

    private User requester;
    private ItemDto dto;
    private User owner;
    private Item item = new Item();
    private ItemForUserDto itemForUserDto;

    @BeforeEach
    void setUp() {
        long ownerId = 1L;
        long requesterId = 2L;

        owner = User.builder()
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

        dto = ItemDto.builder()
                .id(1L)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();

        itemForUserDto = ItemForUserDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .lastBooking(new BookerAndItemDto(1, 2))
                .nextBooking(new BookerAndItemDto(2, 2))
                .build();
    }

    @Test
    void mapToItemDto() {
        ItemDto actualDto = ItemMapper.mapToItemDto(item);

        assertEquals(actualDto, dto);
    }

    @Test
    void testMapToItemDtoWithRequestId() {
        dto.setRequestId(requester.getId());
        ItemDto actualDto = ItemMapper.mapToItemDto(item, requester.getId());

        assertEquals(actualDto, dto);
    }

    @Test
    void mapToItem() {
        Item actualItem = ItemMapper.mapToItem(dto, owner);

        assertEquals(actualItem, item);
    }

    @Test
    void testMapToItem() {
        ItemRequest request = new ItemRequest();
        item.setItemRequest(request);
        Item actualItem = ItemMapper.mapToItem(dto, owner, request);

        assertEquals(actualItem, item);
    }

    @Test
    void mapToItemForUserDto() {
        BookerAndItemDto lastBooking = new BookerAndItemDto(1, 2);
        BookerAndItemDto nextBooking = new BookerAndItemDto(2, 2);

        ItemForUserDto actualItem = ItemMapper.mapToItemForUserDto(item, lastBooking, nextBooking);

        assertEquals(actualItem, itemForUserDto);
    }
}