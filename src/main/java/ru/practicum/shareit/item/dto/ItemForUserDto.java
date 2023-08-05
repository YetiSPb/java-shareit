package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookerAndItemDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemForUserDto {
    private long id;

    private String name;

    private String description;

    private Boolean available;

    private BookerAndItemDto lastBooking;

    private BookerAndItemDto nextBooking;

    private List<CommentDto> comments = new ArrayList<>();
}