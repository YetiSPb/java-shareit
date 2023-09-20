package shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    @NotNull
    private long itemId;
    @NotNull(message = "Дата начала аренды не может быть пустой")
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull(message = "Дата конца аренды не может быть пустой")
    @Future
    private LocalDateTime end;
}
