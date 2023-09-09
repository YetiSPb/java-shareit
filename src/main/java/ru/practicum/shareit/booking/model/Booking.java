package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User booker;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    @Column(name = "start_date")
    private LocalDateTime orderedOn;

    @Column(name = "end_date")
    private LocalDateTime returnedOn;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id == booking.id && Objects.equals(booker, booking.booker) && Objects.equals(item, booking.item) && Objects.equals(orderedOn, booking.orderedOn) && Objects.equals(returnedOn, booking.returnedOn) && status == booking.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, booker, item, orderedOn, returnedOn, status);
    }
}
