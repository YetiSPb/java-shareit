package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private User requester;
    private Booking booking;
    private final Pageable page = PageRequest.of(0, 10);
    private final LocalDateTime now =
            LocalDateTime.of(2023, Month.AUGUST, 4, 15, 16, 1);

    @BeforeEach
    void setUp() {
        item = new Item();

        User owner = User.builder()
                .name("TestRob")
                .email("test2@test.ru")
                .items(new HashSet<>())
                .build();

        userRepository.save(owner);

        requester = User.builder()
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

        booking = Booking.builder()
                .booker(requester)
                .status(Status.WAITING)
                .item(item)
                .start(LocalDateTime.of(2023, Month.SEPTEMBER, 4, 15, 16, 1))
                .end(LocalDateTime.of(2023, Month.OCTOBER, 4, 15, 16, 1))
                .build();

        bookingRepository.save(booking);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findById() {
        Booking actual = bookingRepository.findById(booking.getId());

        assertEquals(actual, booking);
    }

    @Test
    void findAllByBookerOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findAllByBookerOrderByStartDesc(requester, page);
        Booking actualBooking = actualBookings.get(0);

        assertEquals(actualBooking, booking);
        assertThat(actualBookings.size(), is(1));
    }

    @Test
    void findAllByBookerAndStartAfterOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository
                .findAllByBookerAndStartAfterOrderByStartDesc(requester, now, page);
        Booking actualBooking = actualBookings.get(0);

        assertEquals(actualBooking, booking);
        assertThat(actualBookings.size(), is(1));
    }

    @Test
    void findAllByBookerAndEndBeforeOrderByEndDesc() {
        List<Booking> actualBookings = bookingRepository
                .findAllByBookerAndEndBeforeOrderByEndDesc(requester, now.plusYears(1), page);
        Booking actualBooking = actualBookings.get(0);

        assertEquals(actualBooking, booking);
        assertThat(actualBookings.size(), is(1));
    }

    @Test
    void findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository
                .findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(requester,
                        now.plusMonths(2), now.plusMonths(1), page);
        Booking actualBooking = actualBookings.get(0);

        assertEquals(actualBooking, booking);
        assertThat(actualBookings.size(), is(1));
    }

    @Test
    void findAllByBookerAndStatusEqualsOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository
                .findAllByBookerAndStatusEqualsOrderByStartDesc(requester,
                        Status.WAITING, page);
        Booking actualBooking = actualBookings.get(0);

        assertEquals(actualBooking, booking);
        assertThat(actualBookings.size(), is(1));
    }

    @Test
    void findAllByItemInOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository
                .findAllByItemInOrderByStartDesc(List.of(item), page);
        Booking actualBooking = actualBookings.get(0);

        assertEquals(actualBooking, booking);
        assertThat(actualBookings.size(), is(1));
    }

    @Test
    void findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository
                .findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(List.of(item), now.plusMonths(2),
                        now.plusMonths(1), page);
        Booking actualBooking = actualBookings.get(0);

        assertEquals(actualBooking, booking);
        assertThat(actualBookings.size(), is(1));
    }

    @Test
    void findAllByItemInAndEndBeforeOrderByEndDesc() {
        List<Booking> actualBookings = bookingRepository
                .findAllByItemInAndEndBeforeOrderByEndDesc(List.of(item), now.plusMonths(4), page);
        Booking actualBooking = actualBookings.get(0);

        assertEquals(actualBooking, booking);
        assertThat(actualBookings.size(), is(1));
    }

    @Test
    void findAllByItemInAndStartAfterOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository
                .findAllByItemInAndStartAfterOrderByStartDesc(List.of(item), now, page);
        Booking actualBooking = actualBookings.get(0);

        assertEquals(actualBooking, booking);
        assertThat(actualBookings.size(), is(1));
    }

    @Test
    void findAllByItemInAndStatusEqualsOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository
                .findAllByItemInAndStatusEqualsOrderByStartDesc(List.of(item), Status.WAITING, page);
        Booking actualBooking = actualBookings.get(0);

        assertEquals(actualBooking, booking);
        assertThat(actualBookings.size(), is(1));
    }

    @Test
    void findAllByItemAndStatusOrderByEndAsc() {
        List<Booking> actualBookings = bookingRepository
                .findAllByItemAndStatusOrderByEndAsc(item, Status.WAITING);
        Booking actualBooking = actualBookings.get(0);

        assertEquals(actualBooking, booking);
        assertThat(actualBookings.size(), is(1));
    }
}