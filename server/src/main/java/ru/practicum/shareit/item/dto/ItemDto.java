package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Comment;

import java.util.Set;

@Data
@Builder
public class ItemDto {
    private long id;

    private String name;

    private String description;

    private Boolean available;

    private Set<Comment> comments;

    private Long requestId;
}
