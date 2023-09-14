package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookerAndItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUserDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        User user = checkUserId(userId);
        Item item = ItemMapper.mapToItem(itemDto, user);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId, Long requestId) {
        User user = checkUserId(userId);
        ItemRequest itemRequest = checkItemRequestId(requestId);
        Item item = ItemMapper.mapToItem(itemDto, user, itemRequest);
        return ItemMapper.mapToItemDto(itemRepository.save(item), requestId);
    }

    @Override
    public ItemDto partialUpdateItem(Map<String, Object> updates, long itemId, long userId) {
        checkUserId(userId);
        Item item = checkItemId(itemId);
        if (item.getUser().getId() != userId) {
            throw new DataNotFoundException("У пользователя по id " + userId + " нет такой вещи по id " + item.getId());
        }
        return ItemMapper.mapToItemDto(itemRepository.save(patchItem(updates, item)));
    }

    @Override
    public ItemForUserDto findById(long itemId, long userId) {
        checkUserId(userId);
        Item item = checkItemId(itemId);

        ItemForUserDto itemForUserDto = ItemMapper.mapToItemForUserDto(item, null, null);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        List<CommentDto> commentDtos = comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
        itemForUserDto.setComments(commentDtos);

        if (checkIfUserIsOwner(userId, item)) {
            return updateUserInfoById(item, commentDtos);
        } else {
            return itemForUserDto;
        }
    }

    @Override
    public List<ItemForUserDto> findAllItems(Long userId, Pageable page) {
        checkUserId(userId);
        return itemRepository.findAllItemsByUserId(userId, page).stream()
                .map(item -> updateUserInfoById(item, item.getComments()
                        .stream()
                        .map(CommentMapper::mapToCommentDto)
                        .collect(Collectors.toList())))
                .sorted(Comparator.comparing(ItemForUserDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text, Long userId, Pageable page) {
        checkUserId(userId);
        return itemRepository.searchItemsByNameOrDescriptionContainingIgnoreCase(text, text, page)
                .stream()
                .filter(Item::isAvailable)
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = checkUserId(userId);
        Item item = checkItemId(itemId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndEndBeforeOrderByEndDesc(userId,
                itemId, now);

        if (bookings == null || bookings.size() == 0) {
            throw new ValidationException("Пользователь с id " + userId +
                    " не брал в аренду вещь по id " + itemId);
        }

        if (bookings.get(0).getEnd().isAfter(now)) { // если у последней аренды срок еще не вышел
            throw new ValidationException("Пользователь с id " + userId +
                    " еще не вернул из аренды вещь по id " + itemId);
        }

        Comment comment = CommentMapper.mapToComment(commentDto, user, item);
        comment.setCreated(now);
        commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(comment);
    }

    private User checkUserId(long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new DataNotFoundException("Пользователя с id " + userId + " нет в базе данных");
        }
        return user;
    }

    private ItemRequest checkItemRequestId(Long itemRequestId) {
        return itemRequestRepository.findById(itemRequestId).orElseThrow(() ->
                new DataNotFoundException("Запроса по id " + itemRequestId + " нет в базе данных"));
    }

    private Boolean checkIfUserIsOwner(long userId, Item item) {
        return item.getUser().getId() == userId;
    }

    private Item checkItemId(long itemId) {
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new DataNotFoundException("Вещи с таким id нет в базе");
        }
        return item;
    }

    private ItemForUserDto updateUserInfoById(Item item, List<CommentDto> commentDtos) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByEndAsc(item, Status.APPROVED);
        if (bookings == null || bookings.size() == 0) {
            ItemForUserDto i1 = ItemMapper.mapToItemForUserDto(item, null, null);
            i1.setComments(commentDtos);
            return i1;
        } else if (bookings.size() == 1) {
            Booking booking = bookings.get(0);
            BookerAndItemDto bookerAndItemDto = BookingMapper.mapToBookerAndItemDto(booking);
            ItemForUserDto i1 = ItemMapper.mapToItemForUserDto(item, bookerAndItemDto, null);
            i1.setComments(commentDtos);
            return i1;
        } else {
            Booking b1 = bookings.get(0); // берем минимальный
            Booking b2 = bookings.get(bookings.size() - 1); // и максимальный

            for (Booking booking : bookings) {
                if (booking.getEnd().isBefore(now)) {
                    if (booking.getEnd().isAfter(b1.getEnd())) { // если он до наст времени, но позже минимального
                        b1 = booking; // это будет наш ласт
                    }
                }
                if (booking.getStart().isAfter(now)) {
                    if (booking.getEnd().isBefore(b2.getEnd())) {
                        b2 = booking; // наш некст
                    }
                }
            }
            BookerAndItemDto last = BookingMapper.mapToBookerAndItemDto(b1);
            BookerAndItemDto next = BookingMapper.mapToBookerAndItemDto(b2);
            ItemForUserDto i1 = ItemMapper.mapToItemForUserDto(item, last, next);
            i1.setComments(commentDtos);
            return i1;
        }
    }

    private Item patchItem(Map<String, Object> updates, Item itemOld) {
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
        return itemOld;
    }
}
