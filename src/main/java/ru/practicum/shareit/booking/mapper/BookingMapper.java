package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookerAndItemDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "bookerId", source = "booker.id")
    @Mapping(target = "start", source = "orderedOn")
    @Mapping(target = "end", source = "returnedOn")
    BookingDto toDTO(Booking booking);

    @Mapping(target = "id", source = "bookingDto.id")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "orderedOn", source = "bookingDto.start")
    @Mapping(target = "returnedOn", source = "bookingDto.end")
    Booking toModel(BookingDto bookingDto, User booker, Item item, Status status);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookerAndItemDto toBookerAndItemDto(Booking booking);
}
