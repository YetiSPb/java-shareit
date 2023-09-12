package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Item findById(long itemId);

    @Query("select i from Item as i " +
            "JOIN FETCH i.user as u " +
            "where i.user.id = ?1")
    List<Item> findAllItemsByUser(Long userId);

    List<Item> findAllItemsByUserId(Long userId, Pageable page);

    List<Item> searchItemsByNameOrDescriptionContainingIgnoreCase(String name, String description, Pageable page);

    List<Item> findAllByItemRequestId(long requestId);
}
