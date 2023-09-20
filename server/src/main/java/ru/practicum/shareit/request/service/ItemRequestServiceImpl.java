package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto dto) {
        LocalDateTime now = LocalDateTime.now();
        User user = checkUserId(userId);
        dto.setRequesterId(user.getId());
        dto.setCreated(now);
        List<Item> items = new ArrayList<>(itemRepository.findAllByItemRequestId(dto.getId()));

        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(dto, user);
        itemRequest.setItems(items);

        return ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findAllOwnRequests(Long userId) {
        checkUserId(userId);

        return itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(toList());
    }

    @Override
    public List<ItemRequestDto> findAllItemRequests(Long userId, int from, int size) {
        checkUserId(userId);
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(from, size, sortByCreated);


        return itemRequestRepository.findAllByRequesterIdIsNot(userId, page).stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(toList());
    }

    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        checkUserId(userId);
        ItemRequest itemRequest = checkItemRequestId(requestId);
        List<Item> items = new ArrayList<>(itemRepository.findAllByItemRequestId(requestId));
        itemRequest.setItems(items);
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    private User checkUserId(long userId) {
        User user = userRepository.findById(userId);

        if (user == null) {
            throw new DataNotFoundException("Пользователя с id " + userId + " нет в базе данных");
        }
        return user;
    }

    private ItemRequest checkItemRequestId(long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Request с id " + id + " нет в базе данных"));
    }
}
