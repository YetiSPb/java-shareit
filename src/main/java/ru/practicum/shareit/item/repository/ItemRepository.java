package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i from Item as i " +
            "JOIN FETCH i.user as u " +
            "where i.user.id = ?1")
    List<Item> findAllItemsByUser(Long userId);

    Optional<Item> findByIdAndUser_IdNot(Long id, Long id1);

    Optional<List<Item>> findByDescriptionContainingIgnoreCase(String description);

    Optional<List<Item>> findByDescriptionContainingIgnoreCaseAndAvailable(String description, boolean available);

    List<Item> findByUser_Id(Long id);

}
