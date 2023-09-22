package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(long userId, BookingDto bookingDto);

    BookingDto approveBooking(long userId, long bookingId, boolean approved);

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> findAllBookingsByUser(Long userId, BookingState state, Pageable page);

    List<BookingDto> findAllBookingsByOwner(Long userId, BookingState state, Pageable page);
}
