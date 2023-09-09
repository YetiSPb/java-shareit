package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
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