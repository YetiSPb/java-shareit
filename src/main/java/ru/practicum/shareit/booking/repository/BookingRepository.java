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
    List<Booking> findByItem_User_IdAndOrderedOnBeforeAndReturnedOnAfterOrderByOrderedOnDesc(long id, LocalDateTime orderedOn, LocalDateTime returnedOn, Pageable page);

    List<Booking> findByItem_User_IdOrderByOrderedOnDesc(long id, Pageable page);

    List<Booking> findAllByBookerOrderByOrderedOnDesc(User booker, Pageable page);

    List<Booking> findAllByBookerAndOrderedOnAfterOrderByOrderedOnDesc(User booker, LocalDateTime start, Pageable page);

    List<Booking> findAllByBookerAndReturnedOnBeforeOrderByReturnedOnDesc(User booker, LocalDateTime end, Pageable page);

    List<Booking> findAllByBookerAndOrderedOnBeforeAndReturnedOnAfterOrderByOrderedOnDesc(User booker, LocalDateTime start, LocalDateTime end, Pageable page);

    List<Booking> findAllByBookerAndStatusEqualsOrderByOrderedOnDesc(User user, Status status, Pageable page);

    List<Booking> findAllByItemAndStatusOrderByReturnedOnAsc(Item item, Status status);

    List<Booking> findByBookerIdAndItemIdAndReturnedOnBeforeOrderByReturnedOnDesc(long bookerId, long itemId, LocalDateTime end);

    List<Booking> findByItem_User_IdAndReturnedOnBeforeOrderByReturnedOnDesc(long bookerId, LocalDateTime now, Pageable page);

    List<Booking> findByItem_User_IdAndOrderedOnAfterOrderByOrderedOnDesc(long bookerId, LocalDateTime now, Pageable page);

    List<Booking> findByItem_User_IdAndStatusEqualsOrderByOrderedOnDesc(long bookerId, Status status, Pageable page);

}
