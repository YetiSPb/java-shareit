package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemRepository {
    ItemDto save(Item item);

    Item findById(long itemId);

    ItemDto partialUpdateItem(Map<String, Object> updates, Item itemOld);

    List<Item> findAllItems(Long userId);

    List<ItemDto> searchItems(String text, Long userId);
}
