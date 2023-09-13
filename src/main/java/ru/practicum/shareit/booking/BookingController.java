package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.repository.OffsetLimitPageable;

import javax.validation.Valid;
import java.util.List;


@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody BookingDto bookingDto) {
        log.debug("Поступил POST запрос на создание брони для вещи по id {}", bookingDto.getItemId());
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationException("Время открытия аренды не может быть позже или равно сдачи вещи");
        }
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        log.debug("Поступил запрос PATCH на одобрение брони от пользователя по id {} на бронь по id {}",
                userId, bookingId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable(required = false) Long bookingId) {
        log.debug("Поступил запрос GET на получение брони по id {} от пользователя по id {}",
                bookingId, userId);
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> findAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "20") int size) {
        log.debug("Поступил запрос GET на получение всех бронирований с параметром {}", state);
        BookingState status = BookingState.from(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + state));
        Pageable page = OffsetLimitPageable.of(from, size);
        return bookingService.findAllBookingsByUser(userId, status, page);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "20") int size) {
        log.debug("Поступил запрос GET на получение всех бронирований владельца с параметром {}", state);
        BookingState status = BookingState.from(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + state));
        Pageable page = OffsetLimitPageable.of(from, size);
        return bookingService.findAllBookingsByOwner(userId, status, page);
    }
}
