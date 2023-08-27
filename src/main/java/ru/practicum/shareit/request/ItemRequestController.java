package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping()
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemRequestDto dto) {
        log.debug("Поступил POST запрос на создание request {} от пользователя по id {}",
                dto.toString(), userId);
        return itemRequestService.addItemRequest(userId, dto);
    }

    @GetMapping()
    public List<ItemRequestDto> findAllOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Поступил GET запрос на получение всех своих запросов пользователя по id {}", userId);
        return itemRequestService.findAllOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        log.debug("Поступил GET запрос на получение всех запросов от {} по {}", from, size);
        return itemRequestService.findAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable(required = false) Long requestId) {
        log.debug("Получен GET запрос на получение request по id {} от пользователя по id {}",
                requestId, userId);
        return itemRequestService.findById(userId, requestId);
    }

}