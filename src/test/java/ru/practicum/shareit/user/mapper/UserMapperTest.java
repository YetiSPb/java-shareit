package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private UserDto dto;
    private User owner;
    private Item item = new Item();

    @BeforeEach
    void setUp() {
        long ownerId = 1L;

        owner = User.builder()
                .id(ownerId)
                .name("TestRob")
                .email("test2@test.ru")
                .items(Set.of(item))
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

        dto = UserDto.builder()
                .id(ownerId)
                .name(owner.getName())
                .email(owner.getEmail())
                .build();
    }

    @Test
    void mapToUserDto() {
        UserDto actualUserDto = UserMapper.mapToUserDto(owner);

        assertEquals(actualUserDto, dto);
    }

    @Test
    void mapToUser() {
        User actualUser = UserMapper.mapToUser(dto);

        assertThat(actualUser.getId(), is(dto.getId()));
        assertThat(actualUser.getName(), is(dto.getName()));
        assertThat(actualUser.getEmail(), is(dto.getEmail()));
    }
}