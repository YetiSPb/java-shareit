package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class Booking {
    private long id;
    private User booker;
    private Item item;
    private LocalDateTime orderedOn;
    private LocalDateTime returnedOn;
    private Status status;
}
