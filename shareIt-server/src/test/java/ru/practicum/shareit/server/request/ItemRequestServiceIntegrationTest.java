package ru.practicum.shareit.server.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.request.model.ExtendedItemRequest;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitConfig
public class ItemRequestServiceIntegrationTest {


    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private User requester;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        requester = createUser("Jan Jack De Jui", "Jui@example.com");
        itemRequest = createItemRequest(requester);
    }

    @Test
    void givenUserAndItem_whenCreatingItemRequest_thenItemRequestCreated() {

        Item item = createItem("Small Dar", true, itemRequest.getId());

        List<ItemRequest> userRequests = itemRequestService.getUserRequests(requester.getId());

        assertEquals(1, userRequests.size());
        assertEquals(itemRequest.getId(), userRequests.get(0).getId());

        ExtendedItemRequest extendedItemRequest = itemRequestService.getExtendedRequest(userRequests.get(0));
        assertNotNull(extendedItemRequest);
        assertEquals(1, extendedItemRequest.getResponses().size());
        assertEquals(item.getId(), extendedItemRequest.getResponses().get(0).getResponseItem().getId());
    }

    @Test
    void givenExistingItemRequest_whenGettingExtendedRequest_thenExtendedRequestReturned() {
        Item item = createItem("Big Dar", false, itemRequest.getId());

        ExtendedItemRequest extendedItemRequest = itemRequestService.getExtendedRequest(itemRequest);

        assertNotNull(extendedItemRequest);
        assertEquals(1, extendedItemRequest.getResponses().size());
        assertEquals(item.getId(), extendedItemRequest.getResponses().get(0).getResponseItem().getId());
    }

    @Test
    void givenUserRequestsExist_whenGettingUserRequests_thenUserRequestsReturned() {
        List<ItemRequest> userRequests = itemRequestService.getUserRequests(requester.getId());

        assertNotNull(userRequests);
        assertEquals(1, userRequests.size());
        assertEquals(itemRequest.getId(), userRequests.get(0).getId());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userService.create(user);
    }

    private ItemRequest createItemRequest(User requester) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(requester);
        return itemRequestService.create(requester.getId(), itemRequest);
    }

    private Item createItem(String name, boolean available, Long requestId) {
        User owner = createUser("Jan Jack De Jack", "Jack@example.com");
        Item item = new Item();
        item.setName(name);
        item.setAvailable(available);
        item.setRequestId(requestId);
        return itemService.create(item, owner.getId());
    }
}
