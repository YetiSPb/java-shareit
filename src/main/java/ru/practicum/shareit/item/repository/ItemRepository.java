package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(Item item);

    Item findById(long itemId);

    Item partialUpdateItem(Item updates);

    List<Item> findAllItems(Long userId);

    List<ItemDto> searchItems(String text, Long userId);
}
