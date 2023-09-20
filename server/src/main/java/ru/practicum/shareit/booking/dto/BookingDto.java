package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingDto {

    private long id;

    private Long itemId;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookerDto booker;

    private BookItemDto item;

    private Status status;

    @JsonCreator
    public BookingDto(long id, LocalDateTime start, LocalDateTime end, BookerDto booker, BookItemDto item, Status status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.booker = booker;
        this.item = item;
        this.status = status;
    }
}
