package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUserDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @Valid @RequestBody ItemDto itemDto) {
        log.debug("Поступил запрос POST на создание вещи {} от пользователя по id {}",
                itemDto.toString(), userId);
        if (itemDto.getRequestId() == null) {
            return itemService.saveItem(itemDto, userId);
        } else {
            return itemService.saveItem(itemDto, userId, itemDto.getRequestId());
        }
    }

    @PatchMapping("/{itemId}")
    public ItemDto partialUpdateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody Map<String, Object> updates,
                                     @PathVariable(required = false) Long itemId) {
        log.debug("Получен запрос PATCH на обновление вещи по id {}", itemId);
        return itemService.partialUpdateItem(updates, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemForUserDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable(required = false) Long itemId) {
        log.debug("Получен запрос GET на получение вещи по id {}", itemId);
        return itemService.findById(itemId, userId);
    }

    @GetMapping
    public List<ItemForUserDto> findAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "20") int size) {
        log.debug("Получен запрос GET на получение вещей пользователя по id {}", userId);
        Pageable page = PageRequest.of(from, size);
        return itemService.findAllItems(userId, page);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam String text,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "20") int size) {
        log.debug("Получен запрос GET на поиск вещей от пользователя по id {}", userId);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Pageable page = PageRequest.of(from, size);
        return itemService.searchItems(text.toLowerCase(), userId, page);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody CommentDto commentDto,
                                 @PathVariable(required = false) Long itemId) {
        log.debug("Получен запрос GET на получение комментариев для вещи по id {} от пользователя по id {}",
                itemId, userId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
