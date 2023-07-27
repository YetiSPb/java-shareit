package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(DataNotFoundException::new);
        Item item = ItemMapper.mapToItem(itemDto, user);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto updates, long itemId, long userId) {
        userRepository.findById(userId).orElseThrow(DataNotFoundException::new);
        Item item = itemRepository.findById(itemId).orElseThrow(DataNotFoundException::new);

        if (item.getOwner().getId() != userId) {
            throw new DataNotFoundException("У пользователя по id " + userId + " нет такой вещи по id " + item.getId());
        }

        if (updates.getName() != null) {
            item.setName(updates.getName());
        }
        if (updates.getDescription() != null) {
            item.setDescription(updates.getDescription());
        }
        if ((updates.getAvailable() != null)) {
            item.setAvailable(updates.getAvailable());
        }

        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto findById(long itemId, long userId) {
        userRepository.findById(userId).orElseThrow(DataNotFoundException::new);
        Item item = itemRepository.findById(itemId).orElseThrow(DataNotFoundException::new);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> findAllItems(Long userId) {
        return itemRepository.findByOwner_Id(userId).stream().map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text,  Boolean accept) {
        List<Item> users;
        if (Boolean.TRUE.equals(accept)) {
            users = itemRepository
                    .findByDescriptionContainingIgnoreCaseAndAvailable(text, true)
                    .orElseThrow(DataNotFoundException::new);
        } else {
            users = itemRepository.findByDescriptionContainingIgnoreCase(text)
                    .orElseThrow(DataNotFoundException::new);
        }
        return users.stream().map(ItemMapper::mapToItemDto).collect(Collectors.toList());
    }

}
