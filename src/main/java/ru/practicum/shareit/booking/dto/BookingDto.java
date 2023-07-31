package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import javax.persistence.Column;
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
    @Column(name = "start_date")
    private LocalDateTime start;

    @NotNull(message = "Дата конца аренды не может быть пустой")
    @FutureOrPresent
    @Column(name = "end_date")
    private LocalDateTime end;

    private BookerDto booker;
    private BookItemDto item;

    private Status status;

    @JsonCreator
    public BookingDto(long id, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }
}