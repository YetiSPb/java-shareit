package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    private Comment comment;
    private CommentDto dto;
    private User requester;
    private Item item = new Item();

    private final LocalDateTime date =
            LocalDateTime.of(2023, Month.AUGUST, 4, 15, 16, 1);

    @BeforeEach
    void setUp() {
        long ownerId = 1L;
        long requesterId = 2L;

        User owner = User.builder()
                .id(ownerId)
                .name("TestRob")
                .email("test2@test.ru")
                .items(Set.of(item))
                .build();

        requester = User.builder()
                .id(requesterId)
                .name("TestBob")
                .email("test@test.ru")
                .items(Set.of(new Item()))
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Perfect Test Item Ever")
                .user(owner)
                .available(true)
                .comments(new ArrayList<>())
                .itemRequest(new ItemRequest())
                .build();

        comment = Comment.builder()
                .id(1L)
                .comment("Some text")
                .author(requester)
                .item(item)
                .created(date)
                .build();

        dto = CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .text(comment.getComment())
                .build();
    }

    @Test
    void mapToComment() {
        Comment actualComment = CommentMapper.mapToComment(dto, requester, item);

        assertEquals(actualComment, comment);
    }

    @Test
    void mapToCommentDto() {
        CommentDto actualDto = CommentMapper.mapToCommentDto(comment);

        assertEquals(actualDto, dto);
    }
}