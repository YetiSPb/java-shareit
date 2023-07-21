package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private long bookingId;
    private User booker;
    private Item item;
    private LocalDateTime orderedOn;
    private LocalDateTime returnedOn;
    private Status status;
}
