package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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
        return itemService.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto partialUpdateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody ItemDto updates,
                                     @PathVariable(required = false) Long itemId) {
        log.debug("Получен запрос PATCH на обновление вещи по id {}", itemId);
        return itemService.partialUpdateItem(updates, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable(required = false) Long itemId) {
        log.debug("Получен запрос GET на получение вещи по id {}", itemId);
        return itemService.findById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> findAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен запрос GET на получение вещей пользователя по id {}", userId);
        return itemService.findAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam String text) {
        log.debug("Получен запрос GET на поиск вещей от пользователя по id {}", userId);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemService.searchItems(text.toLowerCase(), userId);
    }
}
