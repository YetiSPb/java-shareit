package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookerAndItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUserDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto toDTO(Item item);

    @Mapping(source = "itemDto.id", target = "id")
    @Mapping(source = "itemDto.name", target = "name")
    @Mapping(source = "user", target = "user")
    Item toModel(ItemDto itemDto, User user);

    @Mapping(source = "item.id", target = "id")
    ItemForUserDto toItemForUserDto(Item item, BookerAndItemDto lastBooking,
                                    BookerAndItemDto nextBooking);
}
