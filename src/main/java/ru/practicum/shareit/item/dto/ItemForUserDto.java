package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookerAndItemDto;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemForUserDto {
    private long id;

    private String name;

    private String description;

    private Boolean available;

    private BookerAndItemDto lastBooking;

    private BookerAndItemDto nextBooking;
    @Builder.Default
    private List<CommentDto> comments = new ArrayList<>();
}