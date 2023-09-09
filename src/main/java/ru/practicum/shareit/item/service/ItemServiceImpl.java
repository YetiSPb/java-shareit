package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(DataNotFoundException::new);
        Item item = itemMapper.toModel(itemDto, user);
        return itemMapper.toDTO(itemRepository.save(item));
    }

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow(DataNotFoundException::new);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new DataNotFoundException("Запроса по id " + requestId + " нет в базе данных"));
        Item item = itemMapper.toModel(itemDto, user, itemRequest);
        return itemMapper.toDTO(itemRepository.save(item), requestId);
    }

    @Override
    public ItemDto updateItem(ItemDto updates, long itemId, long userId) {
        userRepository.findById(userId).orElseThrow(DataNotFoundException::new);
        Item item = itemRepository.findById(itemId).orElseThrow(DataNotFoundException::new);

        if (item.getUser().getId() != userId) {
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

        return itemMapper.toDTO(itemRepository.save(item));
    }

    @Override
    public ItemForUserDto findById(long itemId, long userId) {
        userRepository.findById(userId).orElseThrow(DataNotFoundException::new);
        Item item = itemRepository.findById(itemId).orElseThrow(DataNotFoundException::new);

        ItemForUserDto itemForUserDto = itemMapper.toItemForUserDto(item, null, null);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        List<CommentDto> commentDtos = comments.stream().map(commentMapper::toDTO).collect(Collectors.toList());
        itemForUserDto.setComments(commentDtos);

        if (Boolean.TRUE.equals(checkIfUserIsOwner(userId, item))) {
            return findItemByIdForUser(item, commentDtos);
        } else {
            return itemForUserDto;
        }
    }


    private ItemForUserDto findItemByIdForUser(Item item, List<CommentDto> commentDtos) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByReturnedOnAsc(item, Status.APPROVED);
        if (bookings.isEmpty()) {
            ItemForUserDto i1 = itemMapper.toItemForUserDto(item, null, null);
            i1.setComments(commentDtos);
            return i1;
        } else if (bookings.size() == 1) {
            Booking booking = bookings.get(0);
            BookerAndItemDto bookerAndItemDto = bookingMapper.toBookerAndItemDto(booking);
            ItemForUserDto i1 = itemMapper.toItemForUserDto(item, bookerAndItemDto, null);
            i1.setComments(commentDtos);
            return i1;
        } else {
            Booking b1 = bookings.get(0);
            Booking b2 = bookings.get(bookings.size() - 1);

            for (Booking booking : bookings) {
                if (booking.getReturnedOn().isBefore(now)) {
                    if (booking.getReturnedOn().isAfter(b1.getReturnedOn())) {
                        b1 = booking;
                    }
                }
                if (booking.getOrderedOn().isAfter(now)) {
                    if (booking.getReturnedOn().isBefore(b2.getReturnedOn())) {
                        b2 = booking;
                    }
                }
            }
            BookerAndItemDto last = bookingMapper.toBookerAndItemDto(b1);
            BookerAndItemDto next = bookingMapper.toBookerAndItemDto(b2);
            ItemForUserDto i1 = itemMapper.toItemForUserDto(item, last, next);
            i1.setComments(commentDtos);
            return i1;
        }
    }

    @Override
    public List<ItemForUserDto> findAllItems(Long userId) {
        userRepository.findById(userId).orElseThrow(DataNotFoundException::new);
        return itemRepository.findByUser_Id(userId).stream()
                .map(item -> findItemByIdForUser(item, item.getComments()
                        .stream()
                        .map(commentMapper::toDTO)
                        .collect(Collectors.toList())))
                .sorted(Comparator.comparing(ItemForUserDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text, Boolean accept) {
        List<Item> users;
        if (Boolean.TRUE.equals(accept)) {
            users = itemRepository
                    .findByDescriptionContainingIgnoreCaseAndAvailable(text, true)
                    .orElseThrow(DataNotFoundException::new);
        } else {
            users = itemRepository.findByDescriptionContainingIgnoreCase(text)
                    .orElseThrow(DataNotFoundException::new);
        }
        return users.stream().map(itemMapper::toDTO).collect(Collectors.toList());
    }

    private Boolean checkIfUserIsOwner(long userId, Item item) {
        return item.getUser().getId() == userId;
    }


    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(DataNotFoundException::new);
        Item item = itemRepository.findById(itemId).orElseThrow(DataNotFoundException::new);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndReturnedOnBeforeOrderByReturnedOnDesc(userId,
                itemId, now);

        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь с id " + userId +
                    " не брал в аренду вещь по id " + itemId);
        }

        if (bookings.get(0).getReturnedOn().isAfter(now)) {
            throw new ValidationException("Пользователь с id " + userId +
                    " еще не вернул из аренды вещь по id " + itemId);
        }

        Comment comment = commentMapper.toModel(commentDto, user, item);
        comment.setCreated(now);
        commentRepository.save(comment);

        return commentMapper.toDTO(comment);
    }

}
