package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {

    private User requester;
    private Item item = new Item();
    private ItemRequest itemRequest;
    private ItemRequestDto dto;
    private final LocalDateTime date =
            LocalDateTime.of(2023, Month.AUGUST, 4, 15, 16, 1);

    @BeforeEach
    void setUp() {
        long ownerId = 1L;
        long requesterId = 2L;

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

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Some description")
                .created(date)
                .requester(requester)
                .items(List.of(new Item()))
                .build();

        List<ItemDto> dtos = itemRequest.getItems().stream()
                .map(item -> ItemMapper.mapToItemDto(item, itemRequest.getId()))
                .collect(Collectors.toList());

        dto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requesterId(itemRequest.getRequester().getId())
                .items(dtos)
                .build();
    }

    @Test
    void mapToItemRequest() {
        ItemRequest actualRequest = ItemRequestMapper.mapToItemRequest(dto, requester);

        assertEquals(actualRequest, itemRequest);
    }

    @Test
    void mapToItemRequestDto() {
        ItemRequestDto actualDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);

        assertEquals(actualDto, dto);
    }
}