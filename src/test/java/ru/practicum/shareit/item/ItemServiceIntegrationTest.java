package ru.practicum.shareit.item;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    void getUserItems_ShouldReturnUserItems() {

        // Создание владельца вещи
        User owner = new User();
        owner.setName("Jan Jack De Jack");
        owner.setEmail("Jack@example.com");
        User savedOwner = userService.create(owner);

        // Создание вещи
        Item item1 = new Item();
        item1.setName("Big Dar");
        item1.setAvailable(true);
        Item savedItem1 = itemService.create(item1, owner.getId());
        assertEquals(savedItem1, entityManager.find(Item.class, savedItem1.getId()));
        Item item2 = new Item();
        item2.setName("Small Dar");
        item2.setAvailable(true);
        Item savedItem2 = itemService.create(item2, owner.getId());
        assertEquals(savedItem2, entityManager.find(Item.class, savedItem2.getId()));


        // Вызов метода сервиса для получения предметов пользователя
        Collection<Item> userItems = itemService.getItemsByOwner(savedOwner.getId(), 0, 10);

        // Проверка, что вернулись все предметы пользователя
        assertEquals(2, userItems.size());
    }
}
