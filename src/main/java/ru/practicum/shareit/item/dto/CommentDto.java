package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private long id;

    @NotNull(message = "Текст комментария не может быть пустым")
    @NotBlank
    private String text;

    private String authorName;

    private LocalDateTime created;
}
