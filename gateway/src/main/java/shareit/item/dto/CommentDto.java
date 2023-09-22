package shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private long id;

    @NotNull(message = "Текст комментария не может быть пустым")
    @NotBlank
    private String text;

    private String authorName;

    private LocalDateTime created;
}
