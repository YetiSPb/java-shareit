package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUserDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl service;

    private User requester;
    private User owner = new User();
    private ItemRequest itemRequest = new ItemRequest();
    private Comment comment = new Comment();
    private ItemDto dto;
    private Item item = new Item();
    private long requesterId;
    private long ownerId;
    private final LocalDateTime date =
            LocalDateTime.of(2023, Month.AUGUST, 4, 15, 16, 1);

    @BeforeEach
    void setUp() {
        dto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Perfect Test Item Ever")
                .available(true)
                .comments(Set.of(comment))
                .build();

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
                .name("Test Item")
                .description("Perfect Test Item Ever")
                .user(owner)
                .available(true)
                .comments(List.of(comment))
                .itemRequest(itemRequest)
                .build();

        requester = User.builder()
                .id(requesterId)
                .name("TestBob")
                .email("test@test.ru")
                .items(Set.of(new Item()))
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("I want a perfect test item")
                .requester(requester)
                .created(date)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void testSaveItemWithoutRequestId() {
        when(userRepository.findById(ownerId)).thenReturn(owner);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto actualItem = service.saveItem(dto, ownerId);

        assertThat(actualItem.getId(), is(dto.getId()));
        assertThat(actualItem.getName(), is(dto.getName()));
        assertThat(actualItem.getDescription(), is(dto.getDescription()));
        assertThat(actualItem.getAvailable(), is(dto.getAvailable()));
    }

    @Test
    void testSaveItemFailWithWrongRequestId() {
        when(userRepository.findById(ownerId)).thenReturn(owner);
        when(itemRequestRepository.findById(22L)).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        assertThrows(DataNotFoundException.class, () -> service.saveItem(dto, ownerId, 22L));
    }

    @Test
    void testSaveItemWithoutRequestIdNotFoundWhenWrongUserId() {
        long wrongId = 22L;
        when(userRepository.findById(wrongId)).thenThrow(new DataNotFoundException());

        assertThrows(DataNotFoundException.class, () -> service.saveItem(dto, wrongId));
    }

    @Test
    void testSaveItemWithRequestId() {
        dto.setRequestId(requesterId);

        when(userRepository.findById(ownerId)).thenReturn(owner);
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto actualItem = service.saveItem(dto, ownerId, requesterId);

        assertThat(actualItem.getId(), is(dto.getId()));
        assertThat(actualItem.getName(), is(dto.getName()));
        assertThat(actualItem.getDescription(), is(dto.getDescription()));
        assertThat(actualItem.getAvailable(), is(dto.getAvailable()));
        assertThat(actualItem.getRequestId(), is(dto.getRequestId()));
    }

    @Test
    void testSaveItemWithRequestIdNotFoundWithWrongRequestId() {
        dto.setRequestId(requesterId);
        long wrongId = 22L;

        when(userRepository.findById(ownerId)).thenReturn(owner);
        when(itemRequestRepository.findById(wrongId)).thenThrow(new DataNotFoundException());

        assertThrows(DataNotFoundException.class, () -> service.saveItem(dto, ownerId, wrongId));
    }

    @Test
    void testPartialUpdateItemOk() {
        dto.setName("New name");
        dto.setDescription("New Description");
        dto.setAvailable(false);
        Map<String, Object> updates = new HashMap<>();
        updates.put("available", dto.getAvailable());
        updates.put("name", dto.getName());
        updates.put("description", dto.getDescription());
        Item item1 = new Item(dto.getId(), dto.getName(), dto.getDescription(), owner, false,
                new ArrayList<>(), null);

        when(userRepository.findById(ownerId)).thenReturn(owner);
        when(itemRepository.findById(item.getId())).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item1);


        ItemDto actualItem = service.partialUpdateItem(updates, item.getId(), ownerId);

        assertThat(actualItem.getId(), is(dto.getId()));
        assertThat(actualItem.getName(), is(dto.getName()));
        assertThat(actualItem.getDescription(), is(dto.getDescription()));
        assertThat(actualItem.getAvailable(), is(dto.getAvailable()));
        assertThat(actualItem.getRequestId(), is(dto.getRequestId()));

        verify(itemRepository, Mockito.times(1))
                .save(any(Item.class));
    }

    @Test
    void testPartialUpdateItemFailWhenUserIsNotOwner() {
        dto.setName("New name");
        dto.setDescription("New Description");
        dto.setAvailable(false);
        Map<String, Object> updates = new HashMap<>();
        updates.put("available", dto.getAvailable());
        updates.put("name", dto.getName());
        updates.put("description", dto.getDescription());

        when(userRepository.findById(requesterId)).thenReturn(requester);
        when(itemRepository.findById(item.getId())).thenReturn(item);

        assertThrows(DataNotFoundException.class, () -> service.partialUpdateItem(updates, item.getId(), requesterId));
    }

    @Test
    void testPartialUpdateItemFailWhenWrongItemId() {
        dto.setName("New name");
        dto.setDescription("New Description");
        dto.setAvailable(false);
        Map<String, Object> updates = new HashMap<>();
        updates.put("available", dto.getAvailable());
        updates.put("name", dto.getName());
        updates.put("description", dto.getDescription());

        when(userRepository.findById(ownerId)).thenReturn(owner);
        when(itemRepository.findById(22L)).thenReturn(null);

        assertThrows(DataNotFoundException.class, () -> service.partialUpdateItem(updates, 22L, ownerId));
    }

    @Test
    void testFindByIdOkWhenUserIsOwner() {
        comment = Comment.builder()
                .id(1L)
                .comment("it's a wonderful item")
                .author(requester)
                .item(item)
                .created(date)
                .build();
        dto.setComments(Set.of(comment));

        when(userRepository.findById(ownerId)).thenReturn(owner);
        when(itemRepository.findById(item.getId())).thenReturn(item);
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        when(bookingRepository.findAllByItemAndStatusOrderByEndAsc(any(Item.class), any(Status.class)))
                .thenReturn(new ArrayList<>());

        ItemForUserDto result = service.findById(item.getId(), ownerId);
        CommentDto actualComment = result.getComments().get(0);

        assertThat(result.getId(), is(dto.getId()));
        assertThat(result.getName(), is(dto.getName()));
        assertThat(result.getDescription(), is(dto.getDescription()));
        assertThat(result.getAvailable(), is(dto.getAvailable()));
        assertThat(actualComment.getText(), is(comment.getComment()));
        assertThat(actualComment.getCreated(), is(comment.getCreated()));
    }

    @Test
    void testFindByIdFailWhenUserNull() {
        when(userRepository.findById(ownerId)).thenReturn(null);
        when(itemRepository.findById(item.getId())).thenReturn(item);
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        when(bookingRepository.findAllByItemAndStatusOrderByEndAsc(any(Item.class), any(Status.class)))
                .thenReturn(new ArrayList<>());

        assertThrows(DataNotFoundException.class, () -> service.findById(item.getId(), ownerId));

        verify(bookingRepository, never())
                .findAllByItemAndStatusOrderByEndAsc(any(Item.class), any(Status.class));
        verify(userRepository, times(1))
                .findById(ownerId);
        verify(itemRepository, never())
                .findById(item.getId());
        verify(commentRepository, never())
                .findAllByItemId(item.getId());
    }

    @Test
    void testFindByIdOkWhenUserIsNotOwner() {
        when(userRepository.findById(ownerId)).thenReturn(owner);
        when(itemRepository.findById(item.getId())).thenReturn(item);
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(new ArrayList<>());
        when(bookingRepository.findAllByItemAndStatusOrderByEndAsc(any(Item.class), any(Status.class)))
                .thenReturn(new ArrayList<>());

        ItemForUserDto result = service.findById(item.getId(), ownerId);

        assertThat(result.getId(), is(dto.getId()));
        assertThat(result.getName(), is(dto.getName()));
        assertThat(result.getDescription(), is(dto.getDescription()));
        assertThat(result.getAvailable(), is(dto.getAvailable()));

        verify(bookingRepository, times(1))
                .findAllByItemAndStatusOrderByEndAsc(any(Item.class), any(Status.class));
        verify(userRepository, times(1))
                .findById(ownerId);
        verify(itemRepository, times(1))
                .findById(item.getId());
        verify(commentRepository, times(1))
                .findAllByItemId(item.getId());
    }

    @Test
    void testFindAllItemsOk() {
        dto.setComments(new HashSet<>());
        item.setComments(new ArrayList<>());
        Pageable page = PageRequest.of(0, 20);

        when(userRepository.findById(ownerId)).thenReturn(owner);
        when(itemRepository.findAllItemsByUserId(ownerId, page)).thenReturn(List.of(item));

        List<ItemForUserDto> resultItems = service.findAllItems(ownerId, page);
        ItemForUserDto result = resultItems.get(0);

        assertThat(resultItems.size(), is(1));
        assertThat(result.getId(), is(dto.getId()));
        assertThat(result.getName(), is(dto.getName()));
        assertThat(result.getDescription(), is(dto.getDescription()));
        assertThat(result.getAvailable(), is(dto.getAvailable()));

        verify(userRepository, times(1))
                .findById(ownerId);
        verify(itemRepository, times(1))
                .findAllItemsByUserId(ownerId, page);
    }

    @Test
    void testFindAllItemsOkWithBookings() {
        dto.setComments(new HashSet<>());
        item.setComments(new ArrayList<>());
        Booking booking = Booking.builder()
                .id(0L)
                .item(item)
                .status(Status.APPROVED)
                .start(date)
                .end(date.plusMinutes(2))
                .booker(requester)
                .build();
        Pageable page = PageRequest.of(0, 20);

        when(userRepository.findById(ownerId)).thenReturn(owner);
        when(itemRepository.findAllItemsByUserId(ownerId, page)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemAndStatusOrderByEndAsc(item, Status.APPROVED))
                .thenReturn(List.of(booking));

        List<ItemForUserDto> resultItems = service.findAllItems(ownerId, page);
        ItemForUserDto result = resultItems.get(0);

        assertThat(resultItems.size(), is(1));
        assertThat(result.getId(), is(dto.getId()));
        assertThat(result.getName(), is(dto.getName()));
        assertThat(result.getDescription(), is(dto.getDescription()));
        assertThat(result.getAvailable(), is(dto.getAvailable()));
        assertThat(result.getLastBooking().getBookerId(), is(requesterId));

        verify(userRepository, times(1))
                .findById(ownerId);
        verify(itemRepository, times(1))
                .findAllItemsByUserId(ownerId, page);
        verify(bookingRepository, times(1))
                .findAllByItemAndStatusOrderByEndAsc(item, Status.APPROVED);
    }

    @Test
    void testFindAllItemsOkWithTwoBookings() {
        dto.setComments(new HashSet<>());
        item.setComments(new ArrayList<>());
        Booking booking1 = Booking.builder()
                .id(0L)
                .item(item)
                .status(Status.APPROVED)
                .start(date)
                .end(date.plusMinutes(2))
                .booker(requester)
                .build();

        Booking booking2 = Booking.builder()
                .id(1L)
                .item(item)
                .status(Status.APPROVED)
                .start(date.plusMinutes(3))
                .end(date.plusMinutes(5))
                .booker(requester)
                .build();

        Booking booking3 = Booking.builder()
                .id(2L)
                .item(item)
                .status(Status.APPROVED)
                .start(date.plusYears(1))
                .end(date.plusYears(2))
                .booker(requester)
                .build();
        Pageable page = PageRequest.of(0, 20);

        when(userRepository.findById(ownerId)).thenReturn(owner);
        when(itemRepository.findAllItemsByUserId(ownerId, page)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemAndStatusOrderByEndAsc(item, Status.APPROVED))
                .thenReturn(List.of(booking1, booking2, booking3));

        List<ItemForUserDto> resultItems = service.findAllItems(ownerId, page);
        ItemForUserDto result = resultItems.get(0);

        assertThat(resultItems.size(), is(1));
        assertThat(result.getId(), is(dto.getId()));
        assertThat(result.getName(), is(dto.getName()));
        assertThat(result.getDescription(), is(dto.getDescription()));
        assertThat(result.getAvailable(), is(dto.getAvailable()));
        assertThat(result.getLastBooking().getId(), is(booking2.getId()));
        assertThat(result.getNextBooking().getId(), is(booking3.getId()));
    }

    @Test
    void testSearchItemsOk() {
        String text = "perfect";
        Pageable page = PageRequest.of(0, 20);

        when(userRepository.findById(requesterId)).thenReturn(requester);
        when(itemRepository.searchItemsByNameOrDescriptionContainingIgnoreCase(text, text, page))
                .thenReturn(List.of(item));

        List<ItemDto> resultItems = service.searchItems(text, requesterId, page);
        ItemDto result = resultItems.get(0);

        assertThat(resultItems.size(), is(1));
        assertThat(result.getId(), is(dto.getId()));
        assertThat(result.getName(), is(dto.getName()));
        assertThat(result.getDescription(), is(dto.getDescription()));
        assertTrue(result.getAvailable());
    }

    @Test
    void testAddCommentOk() {
        dto.setComments(new HashSet<>());
        item.setComments(new ArrayList<>());
        Booking booking1 = Booking.builder()
                .id(0L)
                .item(item)
                .status(Status.APPROVED)
                .start(date)
                .end(date.plusMinutes(2))
                .booker(requester)
                .build();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("it's a wonderful item")
                .authorName(requester.getName())
                .created(date)
                .build();

        when(userRepository.findById(requesterId)).thenReturn(requester);
        when(itemRepository.findById(item.getId())).thenReturn(item);
        when(bookingRepository.findByBookerIdAndItemIdAndEndBeforeOrderByEndDesc(anyLong(), anyLong(),
                any(LocalDateTime.class))).thenReturn(List.of(booking1));
        when(commentRepository.save(comment)).thenReturn(comment);

        CommentDto actualComment = service.addComment(requesterId, item.getId(), commentDto);

        assertThat(actualComment.getId(), is(commentDto.getId()));
        assertThat(actualComment.getText(), is(commentDto.getText()));
        assertThat(actualComment.getAuthorName(), is(commentDto.getAuthorName()));
    }

    @Test
    void testAddCommentFailWhenUserHasNotBookedItem() {
        dto.setComments(new HashSet<>());
        item.setComments(new ArrayList<>());
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("it's a wonderful item")
                .authorName(requester.getName())
                .created(date)
                .build();

        when(userRepository.findById(requesterId)).thenReturn(requester);
        when(itemRepository.findById(item.getId())).thenReturn(item);
        when(bookingRepository.findByBookerIdAndItemIdAndEndBeforeOrderByEndDesc(anyLong(), anyLong(),
                any(LocalDateTime.class))).thenReturn(new ArrayList<>());

        assertThrows(ValidationException.class, () -> service.addComment(requesterId, item.getId(), commentDto));

        verify(userRepository, times(1))
                .findById(requesterId);
        verify(itemRepository, times(1))
                .findById(item.getId());
        verify(bookingRepository, times(1))
                .findByBookerIdAndItemIdAndEndBeforeOrderByEndDesc(anyLong(), anyLong(),
                        any(LocalDateTime.class));
    }

    @Test
    void testAddCommentFailWhenUserHasNotYetReturnedItem() {
        dto.setComments(new HashSet<>());
        item.setComments(new ArrayList<>());
        Booking booking1 = Booking.builder()
                .id(0L)
                .item(item)
                .status(Status.APPROVED)
                .start(date)
                .end(date.plusYears(1))
                .booker(requester)
                .build();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("it's a wonderful item")
                .authorName(requester.getName())
                .created(date)
                .build();

        when(userRepository.findById(requesterId)).thenReturn(requester);
        when(itemRepository.findById(item.getId())).thenReturn(item);
        when(bookingRepository.findByBookerIdAndItemIdAndEndBeforeOrderByEndDesc(anyLong(), anyLong(),
                any(LocalDateTime.class))).thenReturn(List.of(booking1));

        assertThrows(ValidationException.class, () -> service.addComment(requesterId, item.getId(), commentDto));

        verify(userRepository, times(1))
                .findById(requesterId);
        verify(itemRepository, times(1))
                .findById(item.getId());
        verify(bookingRepository, times(1))
                .findByBookerIdAndItemIdAndEndBeforeOrderByEndDesc(anyLong(), anyLong(),
                        any(LocalDateTime.class));
    }
}