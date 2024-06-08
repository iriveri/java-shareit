package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemJpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserJpaRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemJpaRepository itemRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Test
    void createItemRequest_ShouldCreateItemRequest() {
        // Создание пользователя
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        // Создание предмета
        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description for Item 1");
        item.setOwnerId(savedUser.getId());
        Item savedItem = itemRepository.save(item);

        // Создание запроса на предмет
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(savedUser);

        // Вызов метода сервиса для создания запроса на предмет
        ItemRequest createdItemRequest = itemRequestService.create(savedUser.getId(), itemRequest);

        // Проверка, что запрос на предмет успешно создан
        List<ItemRequest> retrievedItemRequests = itemRequestService.getUserRequests(savedUser.getId());
        assertEquals(1, retrievedItemRequests.size());
        assertEquals(createdItemRequest.getId(), retrievedItemRequests.get(0).getId());
    }
}
