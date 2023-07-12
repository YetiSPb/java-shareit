package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId);
        Item item = ItemMapper.mapToItem(itemDto, user);
        return itemRepository.save(item);
    }

    @Override
    public ItemDto partialUpdateItem(Map<String, Object> updates, long itemId, long userId) {
        checkUserId(userId);
        Item item = itemRepository.findById(itemId);
        if (item.getOwner().getId() != userId) {
            throw new DataNotFoundException("У пользователя по id " + userId + " нет такой вещи по id " + item.getId());
        }
        return itemRepository.partialUpdateItem(updates, item);
    }

    @Override
    public ItemDto findById(long itemId, long userId) {
        checkUserId(userId);
        Item item = itemRepository.findById(itemId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> findAllItems(Long userId) {
        checkUserId(userId);
        return itemRepository.findAllItems(userId);
    }

    @Override
    public List<ItemDto> searchItems(String text, Long userId) {
        checkUserId(userId);
        return itemRepository.searchItems(text, userId);
    }

    private void checkUserId(Long userId) {
        userRepository.findById(userId);
    }
}
