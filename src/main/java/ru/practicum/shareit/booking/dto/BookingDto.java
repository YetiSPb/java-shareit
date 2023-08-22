package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingDto {

    private long id;

    @NotNull
    private Long itemId;
    private Long bookerId;

    @NotNull(message = "Дата начала аренды не может быть пустой")
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "Дата конца аренды не может быть пустой")
    @FutureOrPresent
    private LocalDateTime end;

    private BookerDto booker;
    private BookItemDto item;

    private Status status;

    public BookingDto(long id, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }
}