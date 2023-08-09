package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ItemRequest {
    private long requestId;
    private String description;
    private User requester;
    private LocalDateTime createdOn;
}
