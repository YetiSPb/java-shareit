package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    private User requester;
    private User owner = new User();
    private ItemRequest itemRequest = new ItemRequest();
    private ItemRequestDto dto;
    private Item item = new Item();
    private long requesterId;
    private long ownerId;

    @InjectMocks
    private ItemRequestServiceImpl service;

    @BeforeEach
    void setUp() {
        requesterId = 1L;
        ownerId = 2L;

        owner = User.builder()
                .id(ownerId)
                .name("TestRob")
                .email("test2@test.ru")
                .items(Set.of(item))
                .build();

        item = Item.builder()
                .id(1)
                .name("Perfect Item")
                .description("Perfect item for a requester")
                .user(owner)
                .available(true)
                .comments(List.of(new Comment()))
                .itemRequest(itemRequest)
                .build();

        requester = User.builder()
                .id(requesterId)
                .name("TestBob")
                .email("test@test.ru")
                .items(Set.of(new Item()))
                .build();

        dto = ItemRequestDto.builder()
                .id(1L)
                .description("I want a perfect test item")
                .requesterId(requesterId)
                .created(LocalDateTime.of(2023, Month.AUGUST, 4, 15, 16, 1))
                .items(new ArrayList<>())
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("I want a perfect test item")
                .requester(requester)
                .created(LocalDateTime.of(2023, Month.AUGUST, 4, 15, 16, 1))
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void testAddItemRequestOk() {
        when(userRepository.findById(requesterId)).thenReturn(requester);
        when(itemRepository.findAllByItemRequestId(dto.getId())).thenReturn(new ArrayList<>());
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto actualRequest = service.addItemRequest(requesterId, dto);

        assertThat(actualRequest.getId(), is(dto.getId()));
        assertThat(actualRequest.getRequesterId(), is(requesterId));
        assertThat(actualRequest.getDescription(), is(dto.getDescription()));
    }

    @Test
    void testAddItemRequestWrongUserIdNotFound() {
        long wrongId = 22L;
        when(userRepository.findById(wrongId)).thenThrow(new DataNotFoundException());

        assertThrows(DataNotFoundException.class, () -> service.addItemRequest(wrongId, dto));
    }

    @Test
    void testFindAllOwnRequestsOk() {
        when(userRepository.findById(requesterId)).thenReturn(requester);
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(requesterId))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> result = service.findAllOwnRequests(requesterId);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(dto.getId()));
        assertThat(result.get(0).getDescription(), is(dto.getDescription()));
    }

    @Test
    void testFindAllItemRequestsOk() {
        ItemRequest anotherRequest = new ItemRequest();
        anotherRequest.setRequester(owner);
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(0, 10, sortByCreated);

        when(userRepository.findById(ownerId)).thenReturn(owner);
        when(itemRequestRepository.findAllByRequesterIdIsNot(ownerId, page)).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> result = service.findAllItemRequests(ownerId, 0, 10);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(dto.getId()));
        assertThat(result.get(0).getDescription(), is(dto.getDescription()));
    }

    @Test
    void testFindByIdOk() {
        when(userRepository.findById(requesterId)).thenReturn(requester);
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.findAllByItemRequestId(itemRequest.getId())).thenReturn(new ArrayList<>());

        ItemRequestDto actualRequest = service.findById(requesterId, itemRequest.getId());

        assertThat(actualRequest.getId(), is(dto.getId()));
        assertThat(actualRequest.getRequesterId(), is(requesterId));
        assertThat(actualRequest.getDescription(), is(dto.getDescription()));
    }

    @Test
    void testFindByIdNotFoundWhenWrongRequestId() {
        long wrongId = 22L;
        when(userRepository.findById(requesterId)).thenReturn(requester);
        when(itemRequestRepository.findById(wrongId)).thenThrow(new DataNotFoundException());

        assertThrows(DataNotFoundException.class, () -> service.findById(requesterId, wrongId));
    }

    @Test
    void testFindByIdNotFoundWhenWrongUserId() {
        long wrongId = 22L;
        when(userRepository.findById(wrongId)).thenThrow(new DataNotFoundException());

        assertThrows(DataNotFoundException.class, () -> service.findById(wrongId, itemRequest.getId()));
    }
}