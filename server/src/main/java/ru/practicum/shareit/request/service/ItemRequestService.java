package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(Long userId, ItemRequestDto dto);

    List<ItemRequestDto> findAllOwnRequests(Long userId);

    List<ItemRequestDto> findAllItemRequests(Long userId, int from, int size);

    ItemRequestDto findById(Long userId, Long requestId);
}
