package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserJpaRepository;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class ItemServiceImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemJpaRepository itemRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Test
    void getUserItems_ShouldReturnUserItems() {
        // Создание пользователя
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        // Создание предметов, принадлежащих пользователю
        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description for Item 1");
        item1.setOwnerId(savedUser.getId());
        entityManager.persist(item1);
        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description for Item 2");
        item2.setOwnerId(savedUser.getId());
        entityManager.persist(item2);

        // Вызов метода сервиса для получения предметов пользователя
        Collection<Item> userItems = itemService.getItemsByOwner(savedUser.getId(), 0, 10);

        // Проверка, что вернулись все предметы пользователя
        assertEquals(2, userItems.size());
    }
}
