package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "commentDto.id", target = "id")
    @Mapping(source = "commentDto.text", target = "comment")
    @Mapping(source = "user", target = "author")
    Comment toModel(CommentDto commentDto, User user, Item item);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "comment.author.name", target = "authorName")
    @Mapping(source = "comment", target = "text")
    CommentDto toDTO(Comment comment);
}
