package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private Comment comment;

    @BeforeEach
    void setUp() {
        item = new Item();

        User owner = User.builder()
                .name("TestRob")
                .email("test2@test.ru")
                .items(new HashSet<>())
                .build();

        userRepository.save(owner);

        User requester = User.builder()
                .name("TestBob")
                .email("test@test.ru")
                .items(new HashSet<>())
                .build();

        userRepository.save(requester);

        item = Item.builder()
                .name("Perfect Item")
                .description("Perfect item for a requester")
                .user(owner)
                .available(true)
                .comments(List.of(new Comment()))
                .itemRequest(null)
                .build();

        itemRepository.save(item);

        comment = Comment.builder()
                .author(requester)
                .comment("what a wonderful item")
                .created(LocalDateTime.of(2023, Month.AUGUST, 4, 15, 16, 1))
                .build();

        commentRepository.save(comment);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void findById() {
        Comment actual = commentRepository.findById(comment.getId());

        assertEquals(actual, comment);
    }

    @Test
    void findAllByItemId() {
        item.setComments(List.of(comment));
        List<Comment> actualComments = commentRepository.findAllByItemId(item.getId());
        Comment actualComment = actualComments.get(0);

        assertEquals(actualComment, comment);
        assertThat(actualComments.size(), is(1));
    }
}