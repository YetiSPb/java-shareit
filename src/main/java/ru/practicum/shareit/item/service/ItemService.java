package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUserDto;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto saveItem(ItemDto itemDto, Long userId);

    ItemDto partialUpdateItem(Map<String, Object> updates, long itemId, long userId);

    ItemForUserDto findById(long itemId, long userId);

    List<ItemForUserDto> findAllItems(Long userId, Pageable page);

    List<ItemDto> searchItems(String text, Long userId, Pageable page);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);

    ItemDto saveItem(ItemDto itemDto, Long userId, Long requestId);
}
