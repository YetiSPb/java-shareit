package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private long requestId;
    private String description;
    private User requester;
    private LocalDateTime createdOn;
}
