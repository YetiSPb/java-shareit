package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long id = 0;

    @Override
    public ItemDto save(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public Item findById(long itemId) {
        Item item = items.get(itemId);

        if (item == null) {
            throw new DataNotFoundException("Вещи с таким id нет в базе");
        }

        return item;
    }

    @Override
    public ItemDto partialUpdateItem(Map<String, Object> updates, Item itemOld) {
        patchItem(updates, itemOld);
        return ItemMapper.mapToItemDto(itemOld);
    }

    @Override
    public List<ItemDto> findAllItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text, Long userId) {
        return items.values().stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text)
                        || item.getName().toLowerCase().contains(text))
                .filter(Item::isAvailable)
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    private void patchItem(Map<String, Object> updates, Item itemOld) {
        for (String s : updates.keySet()) {
            switch (s) {
                case "name":
                    itemOld.setName((String) updates.get(s));
                    break;
                case "description":
                    itemOld.setDescription((String) updates.get(s));
                    break;
                case "available":
                    itemOld.setAvailable((Boolean) updates.get(s));
                    break;
            }
        }
    }
}
