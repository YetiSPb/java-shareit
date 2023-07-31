package ru.practicum.shareit.booking.repository;

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

    List<Booking> findAllByBookerOrderByOrderedOnDesc(User booker);

    List<Booking> findAllByBookerAndOrderedOnAfterOrderByOrderedOnDesc(User booker, LocalDateTime start);

    List<Booking> findAllByBookerAndReturnedOnBeforeOrderByReturnedOnDesc(User booker, LocalDateTime end);

    List<Booking> findAllByBookerAndOrderedOnBeforeAndReturnedOnAfterOrderByOrderedOnDesc(User booker, LocalDateTime start,
                                                                                          LocalDateTime end);

    List<Booking> findAllByBookerAndStatusEqualsOrderByOrderedOnDesc(User user, Status status);

    List<Booking> findByBookerIdAndItemIdAndReturnedOnBeforeOrderByReturnedOnDesc(long bookerId, long itemId, LocalDateTime end);

    List<Booking> findAllByItemInOrderByOrderedOnDesc(List<Item> item);

    List<Booking> findAllByItemInAndOrderedOnBeforeAndReturnedOnAfterOrderByOrderedOnDesc(List<Item> items, LocalDateTime now,
                                                                                          LocalDateTime now1);

    List<Booking> findAllByItemInAndReturnedOnBeforeOrderByReturnedOnDesc(List<Item> items, LocalDateTime now);

    List<Booking> findAllByItemInAndOrderedOnAfterOrderByOrderedOnDesc(List<Item> items, LocalDateTime now);

    List<Booking> findAllByItemInAndStatusEqualsOrderByOrderedOnDesc(List<Item> items, Status status);

    List<Booking> findAllByItemAndStatusOrderByReturnedOnAsc(Item item, Status status);

}
