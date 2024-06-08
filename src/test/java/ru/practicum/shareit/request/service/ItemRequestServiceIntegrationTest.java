package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitConfig
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void createItemRequest_ShouldCreateItemRequest() {
        User user = new User();
        user.setName("Jan Jack De Jui");
        user.setEmail("Jui@example.com");
        User savedUser = userService.create(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(savedUser);

        ItemRequest createdItemRequest = itemRequestService.create(savedUser.getId(), itemRequest);
        assertEquals(createdItemRequest, entityManager.find(ItemRequest.class, createdItemRequest.getId()));

        User owner = new User();
        owner.setName("Jan Jack De Jack");
        owner.setEmail("Jack@example.com");
        User savedOwner = userService.create(owner);

        Item item = new Item();
        item.setName("Ogromniy Dar");
        item.setAvailable(true);
        item.setRequestId(createdItemRequest.getId());
        Item savedItem = itemService.create(item, owner.getId());

        List<ItemRequest> retrievedItemRequests = itemRequestService.getUserRequests(savedUser.getId());
        assertEquals(1, retrievedItemRequests.size());
        assertEquals(createdItemRequest.getId(), retrievedItemRequests.get(0).getId());
    }
}
