package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {
    private long id;

    @NotBlank(message = "Имя вещи не может быть пустым")
    private String name;

    @NotBlank(message = "Описание вещи не может быть пустым")
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    private long requestId;

    @NotNull(message = "Необходимо указать статус доступности вещи для аренды")
    private Boolean available;
}
