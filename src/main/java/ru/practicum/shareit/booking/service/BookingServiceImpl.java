package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto addBooking(long userId, BookingDto bookingDto) {

        User user = getUserOrThrowException(userId);
        Item item = itemRepository.findByIdAndUser_IdNot(bookingDto.getItemId(), user.getId())
                .orElseThrow(() -> new DataNotFoundException("Владелец не может арендовать свою вещь"));

        if (!item.isAvailable()) {
            throw new ValidationException("Вещь с id " + item.getId() + " не доступна для брони");
        }

        Booking booking = bookingMapper.toModel(bookingDto, user, item, Status.WAITING);

        return bookingMapper.toDTO(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
        getUserOrThrowException(userId);
        Booking booking = checkBookingId(bookingId);

        long itemId = booking.getItem().getId();
        if (!itemRepository.findByIdAndUser_Id(itemId, userId).isPresent()) {
            throw new DataNotFoundException("Пользователь с id " + userId + " не владелец вещи по id " + itemId);
        }

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Бронь по id " + bookingId + " уже одобрена");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return bookingMapper.toDTO(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        getUserOrThrowException(userId);
        Booking booking = checkBookingId(bookingId);
        long itemId = booking.getItem().getId();

        if (!itemRepository.findByIdAndUser_Id(itemId, userId).isPresent() && userId != booking.getBooker().getId()) {
            throw new DataNotFoundException("Пользователь с id " + userId + " не владелей или не бронировал " +
                    "вещь по id " + itemId);
        }
        return bookingMapper.toDTO(booking);
    }

    @Override
    public List<BookingDto> findAllBookingsByUser(Long userId, BookingState state, Pageable page) {
        User user = getUserOrThrowException(userId);
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerOrderByOrderedOnDesc(user, page)
                        .stream().map(bookingMapper::toDTO)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByBookerAndOrderedOnBeforeAndReturnedOnAfterOrderByOrderedOnDesc(user,
                                LocalDateTime.now(), LocalDateTime.now(), page)
                        .stream().map(bookingMapper::toDTO)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBookerAndReturnedOnBeforeOrderByReturnedOnDesc(user, LocalDateTime.now(), page)
                        .stream().map(bookingMapper::toDTO)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBookerAndOrderedOnAfterOrderByOrderedOnDesc(user, LocalDateTime.now(), page)
                        .stream().map(bookingMapper::toDTO)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBookerAndStatusEqualsOrderByOrderedOnDesc(user, Status.WAITING, page)
                        .stream().map(bookingMapper::toDTO)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBookerAndStatusEqualsOrderByOrderedOnDesc(user, Status.REJECTED, page)
                        .stream().map(bookingMapper::toDTO)
                        .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public List<BookingDto> findAllBookingsByOwner(Long userId, BookingState state, Pageable page) {

        getUserOrThrowException(userId);

        switch (state) {
            case ALL:
                return bookingRepository.findByItem_User_IdOrderByOrderedOnDesc(userId, page)
                        .stream().map(bookingMapper::toDTO)
                        .collect(Collectors.toList());
            case CURRENT:

                return bookingRepository.findByItem_User_IdAndOrderedOnBeforeAndReturnedOnAfterOrderByOrderedOnDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now(), page)
                        .stream().map(bookingMapper::toDTO)
                        .collect(Collectors.toList());
            case PAST:

                return bookingRepository.findByItem_User_IdAndReturnedOnBeforeOrderByReturnedOnDesc(userId, LocalDateTime.now(), page)
                        .stream().map(bookingMapper::toDTO)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByItem_User_IdAndOrderedOnAfterOrderByOrderedOnDesc(userId, LocalDateTime.now(), page)
                        .stream().map(bookingMapper::toDTO)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByItem_User_IdAndStatusEqualsOrderByOrderedOnDesc(userId, Status.WAITING, page)
                        .stream().map(bookingMapper::toDTO)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByItem_User_IdAndStatusEqualsOrderByOrderedOnDesc(userId, Status.REJECTED, page)
                        .stream().map(bookingMapper::toDTO)
                        .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

    private User getUserOrThrowException(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователя не существует. userId:" + userId));
    }

    private Booking checkBookingId(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Брони не существует. bookingId:" + bookingId));
    }

}
