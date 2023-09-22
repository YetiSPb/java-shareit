package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookItemDto;
import ru.practicum.shareit.booking.dto.BookerAndItemDto;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking mapToBooking(BookingDto bookingDto, User user, Item item, Status status) {
        return Booking.builder()
                .id(bookingDto.getId())
                .booker(user)
                .item(item)
                .status(status)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        BookerDto bookerDto = new BookerDto(booking.getBooker().getId());
        BookItemDto bookItemDto = new BookItemDto(booking.getItem().getId(), booking.getItem().getName());

        return BookingDto.builder()
                .id(booking.getId())
                .booker(bookerDto)
                .item(bookItemDto)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public static BookerAndItemDto mapToBookerAndItemDto(Booking booking) {
        return BookerAndItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
