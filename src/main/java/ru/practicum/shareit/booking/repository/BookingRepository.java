package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findById(long id);

    List<Booking> findAllByBookerOrderByStartDesc(User booker, Pageable page);

    List<Booking> findAllByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime start, Pageable page);

    List<Booking> findAllByBookerAndEndBeforeOrderByEndDesc(User booker, LocalDateTime end, Pageable page);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime start,
                                                                           LocalDateTime end, Pageable page);

    List<Booking> findAllByBookerAndStatusEqualsOrderByStartDesc(User user, Status status, Pageable page);

    List<Booking> findByBookerIdAndItemIdAndEndBeforeOrderByEndDesc(long bookerId, long itemId, LocalDateTime end);

    List<Booking> findAllByItemInOrderByStartDesc(List<Item> item, Pageable page);

    List<Booking> findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(List<Item> items, LocalDateTime now,
                                                                           LocalDateTime now1, Pageable page);

    List<Booking> findAllByItemInAndEndBeforeOrderByEndDesc(List<Item> items, LocalDateTime now, Pageable page);

    List<Booking> findAllByItemInAndStartAfterOrderByStartDesc(List<Item> items, LocalDateTime now, Pageable page);

    List<Booking> findAllByItemInAndStatusEqualsOrderByStartDesc(List<Item> items, Status status, Pageable page);

    List<Booking> findAllByItemAndStatusOrderByEndAsc(Item item, Status status);
}
