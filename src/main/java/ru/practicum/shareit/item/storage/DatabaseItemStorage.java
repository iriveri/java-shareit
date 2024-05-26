package ru.practicum.shareit.item.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collection;

@Repository
@Qualifier("DatabaseItemStorage")
public class DatabaseItemStorage implements ItemStorage {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Long addItem(Item item) {
        em.persist(item);
        return item.getId();
    }

    @Override
    @Transactional
    public void updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        Item item = em.find(Item.class, itemId);
        if (item != null && item.getOwnerId().equals(ownerId)) {
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
            item.setAvailable(itemDto.getAvailable());
            em.merge(item);
        }
    }

    @Override
    public Boolean contains(Long itemId) {
        return em.find(Item.class, itemId) != null;
    }

    @Override
    public Item fetchItem(Long itemId) {
        return em.find(Item.class, itemId);
    }

    @Override
    public Collection<Item> fetchUserItems(Long ownerId) {
        return em.createQuery("SELECT i FROM Item i WHERE i.ownerId = :ownerId", Item.class)
                .setParameter("ownerId", ownerId)
                .getResultList();
    }

    @Override
    public Collection<Item> searchForItems(String text) {
        return em.createQuery("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(:text) OR LOWER(i.description) LIKE LOWER(:text)", Item.class)
                .setParameter("text", "%" + text + "%")
                .getResultList();
    }
}