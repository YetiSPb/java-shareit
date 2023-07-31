package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUserDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @Valid @RequestBody ItemDto itemDto) {
        log.debug("Поступил запрос POST на создание вещи {} от пользователя по id {}",
                itemDto.toString(), userId);
        return itemService.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto updates,
                              @PathVariable(required = false) Long itemId) {
        log.debug("Получен запрос PATCH на обновление вещи по id {}", itemId);
        return itemService.updateItem(updates, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemForUserDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable(required = false) Long itemId) {
        log.debug("Получен запрос GET на получение вещи по id {}", itemId);
        return itemService.findById(itemId, userId);
    }

    @GetMapping
    public List<ItemForUserDto> findAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен запрос GET на получение вещей пользователя по id {}", userId);
        return itemService.findAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam String text
            , @RequestHeader(value = "Accept", required = false) Optional<String> accept) {
        log.debug("Получен запрос GET на поиск вещей от пользователя по id {}", userId);
        if (text.equals("")) {
            return new ArrayList<>();
        }
        return itemService.searchItems(text, accept.isPresent());
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
