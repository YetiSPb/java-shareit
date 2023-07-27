package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto updates, long itemId, long userId);

    ItemDto findById(long itemId, long userId);

    List<ItemDto> findAllItems(Long userId);

    List<ItemDto> searchItems(String text, Boolean accept);
}
