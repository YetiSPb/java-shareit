package ru.practicum.shareit.request.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;


@Component
@AllArgsConstructor
public class ItemRequestMapper {

    private final ItemMapper itemMapper;

    public ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest request = ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .requester(user)
                .build();

        List<Item> items = itemRequestDto.getItems().stream()
                .map(itemDto -> itemMapper.toModel(itemDto, user, request))
                .collect(Collectors.toList());
        request.setItems(items);

        return request;
    }

    public ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        List<ItemDto> dtos = itemRequest.getItems().stream()
                .map(item -> itemMapper.toDTO(item, itemRequest.getId()))
                .collect(Collectors.toList());

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requesterId(itemRequest.getRequester().getId())
                .items(dtos)
                .build();
    }
}