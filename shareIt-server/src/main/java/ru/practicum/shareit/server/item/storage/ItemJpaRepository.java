package ru.practicum.shareit.server.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.item.model.Item;


@Repository
public interface ItemJpaRepository extends JpaRepository<Item, Long> {
    Slice<Item> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE (:text IS NOT NULL AND :text <> '' " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))) " +
            "AND i.available = true")
    Slice<Item> searchForItems(String text, Pageable pageable);
}
