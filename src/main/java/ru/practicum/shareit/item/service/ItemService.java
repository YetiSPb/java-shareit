package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUserDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto itemDto, Long userId);

    ItemDto saveItem(ItemDto itemDto, Long userId, Long requestId);

    ItemDto updateItem(ItemDto updates, long itemId, long userId);

    ItemForUserDto findById(long itemId, long userId);

    List<ItemForUserDto> findAllItems(Long userId);

    List<ItemDto> searchItems(String text, Boolean accept);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
