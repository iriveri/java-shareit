package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemJpaRepository extends JpaRepository<Item, Long> {
    //boolean existsById(Long itemId);
    Collection<Item> findByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<Item> searchForItems(String text);
}
