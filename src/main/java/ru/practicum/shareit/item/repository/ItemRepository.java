package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByIdAndUser_Id(long id, long id1);

    Optional<Item> findByIdAndUser_IdNot(Long id, Long id1);

    Optional<List<Item>> findByDescriptionContainingIgnoreCase(String description);

    Optional<List<Item>> findByDescriptionContainingIgnoreCaseAndAvailable(String description, boolean available, Pageable page);

    List<Item> findByUser_Id(Long id, Pageable page);

    List<Item> findAllByItemRequestId(long requestId);
}
