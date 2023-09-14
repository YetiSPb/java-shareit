package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_items",
            joinColumns = {@JoinColumn(name = "item_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
    )
    @ToString.Exclude
    private User user;

    @Column(name = "available", nullable = false)
    private boolean available;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "items_comments",
            joinColumns = {@JoinColumn(name = "item_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "comment_id", referencedColumnName = "id")}
    )
    @ToString.Exclude
    private List<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "items_requests",
            joinColumns = {@JoinColumn(name = "item_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "request_id", referencedColumnName = "id")}
    )
    @ToString.Exclude
    private ItemRequest itemRequest;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id && available == item.available
                && Objects.equals(name, item.name)
                && Objects.equals(description, item.description)
                && Objects.equals(user, item.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, user, available);
    }
}
