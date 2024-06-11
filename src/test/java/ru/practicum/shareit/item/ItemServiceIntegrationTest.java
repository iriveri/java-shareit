package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserJpaRepository;

import javax.persistence.EntityManager;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitConfig
public class ItemServiceIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserJpaRepository userRepository;

    private User owner;
    private Item savedItem1;
    private Item savedItem2;

    @BeforeEach
    void setUp() {
        // Create owner
        owner = new User();
        owner.setName("Jan Jack De Jack");
        owner.setEmail("Jack@example.com");
        owner = userService.create(owner);

        // Create items
        Item item1 = new Item();
        item1.setName("Big Dar");
        item1.setAvailable(true);
        savedItem1 = itemService.create(item1, owner.getId());
        assertEquals(savedItem1, entityManager.find(Item.class, savedItem1.getId()));

        Item item2 = new Item();
        item2.setName("Small Dar");
        item2.setAvailable(true);
        savedItem2 = itemService.create(item2, owner.getId());
        assertEquals(savedItem2, entityManager.find(Item.class, savedItem2.getId()));
    }

    @Test
    void getUserItems_ShouldReturnUserItems() {
        Collection<Item> userItems = itemService.getItemsByOwner(owner.getId(), 0, 10);

        assertNotNull(userItems);
        assertEquals(2, userItems.size());
        assertEquals(savedItem1, userItems.stream().filter(item -> item.getId().equals(savedItem1.getId())).findFirst().orElse(null));
        assertEquals(savedItem2, userItems.stream().filter(item -> item.getId().equals(savedItem2.getId())).findFirst().orElse(null));
    }

    @Test
    void getUserItems_WithInvalidUserId_ShouldThrowException() {
        Long invalidUserId = -1L;

        assertThrows(RuntimeException.class, () -> {
            itemService.getItemsByOwner(invalidUserId, 0, 10);
        });
    }

    @Test
    void createItem_WithInvalidUserId_ShouldThrowException() {
        Item newItem = new Item();
        newItem.setName("NewItem");
        newItem.setAvailable(true);

        Long invalidUserId = -1L;

        assertThrows(RuntimeException.class, () -> {
            itemService.create(newItem, invalidUserId);
        });
    }

    @Test
    void editItem_ShouldUpdateItem() {
        Item updatedItem = new Item();
        updatedItem.setName("UpdatedName");
        updatedItem.setDescription("UpdatedDescription");

        Item editedItem = itemService.edit(savedItem1.getId(), updatedItem, owner.getId());

        assertNotNull(editedItem);
        assertEquals("UpdatedName", editedItem.getName());
        assertEquals("UpdatedDescription", editedItem.getDescription());
        assertEquals(savedItem1.getId(), editedItem.getId());
    }

    @Test
    void editItem_WithNonOwner_ShouldThrowException() {
        // Create another user who is not the owner
        User nonOwner = new User();
        nonOwner.setName("NonOwner");
        nonOwner.setEmail("nonowner@example.com");
        User savedNonOwner = userService.create(nonOwner);

        Item updatedItem = new Item();
        updatedItem.setName("UpdatedName");

        assertThrows(IllegalArgumentException.class, () -> {
            itemService.edit(savedItem1.getId(), updatedItem, savedNonOwner.getId());
        });
    }

}
