package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.ExtendedItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentJpaRepository;
import ru.practicum.shareit.item.storage.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    @Mock
    private ItemJpaRepository itemRepository;

    @Mock
    private CommentJpaRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingService bookingService;


    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldSaveNewItem() {
        Item newItem = new Item();
        newItem.setName("Item 1");
        newItem.setDescription("Description 1");
        newItem.setOwnerId(1L);

        when(itemRepository.save(any(Item.class))).thenReturn(newItem);

        Item savedItem = itemService.create(newItem, 1L);

        assertNotNull(savedItem);
        assertEquals("Item 1", savedItem.getName());
        assertEquals("Description 1", savedItem.getDescription());
        verify(itemRepository, times(1)).save(newItem);
        verify(userService, times(1)).validate(1L);
    }

    @Test
    void edit_ShouldEditExistingItem() {
        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setName("Item 1");
        existingItem.setDescription("Description 1");
        existingItem.setOwnerId(1L);

        Item newItem = new Item();
        newItem.setName("Updated Item");
        newItem.setDescription("Updated Description");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        Item updatedItem = itemService.edit(1L, newItem, 1L);

        assertNotNull(updatedItem);
        assertEquals("Updated Item", updatedItem.getName());
        assertEquals("Updated Description", updatedItem.getDescription());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void edit_ShouldThrowNotFoundException_WhenItemNotFound() {
        Item newItem = new Item();
        newItem.setName("Updated Item");
        newItem.setDescription("Updated Description");

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.edit(1L, newItem, 1L));
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void edit_ShouldThrowIllegalArgumentException_WhenUserNotOwner() {
        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setName("Item 1");
        existingItem.setDescription("Description 1");
        existingItem.setOwnerId(2L); // Different owner ID

        Item newItem = new Item();
        newItem.setName("Updated Item");
        newItem.setDescription("Updated Description");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        assertThrows(IllegalArgumentException.class, () -> itemService.edit(1L, newItem, 1L));
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void getExtendedItem_ShouldReturnExtendedItem() {
        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(1L);

        ExtendedItem extendedItem = new ExtendedItem(item);
        extendedItem.setComments(List.of());

        when(bookingService.getLastBooking(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(bookingService.getNextBooking(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(commentRepository.findByItemId(1L)).thenReturn(Collections.emptyList());

        ExtendedItem result = itemService.getExtendedItem(item, 1L);

        assertNotNull(result);
        assertEquals(extendedItem, result);
        assertTrue(result.getComments().isEmpty());
        verify(bookingService, times(1)).getLastBooking(anyLong(), anyLong());
        verify(bookingService, times(1)).getNextBooking(anyLong(), anyLong());
        verify(commentRepository, times(1)).findByItemId(1L);
    }

    @Test
    void addComment_ShouldAddComment() {
        Item item = new Item();
        item.setId(1L);

        User user = new User();
        user.setId(1L);

        Comment comment = new Comment();
        comment.setText("Comment text");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.getById(1L)).thenReturn(user);
        when(bookingService.isUserBookedItem(1L, 1L)).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment savedComment = itemService.addComment(1L, 1L, comment);

        assertNotNull(savedComment);
        assertEquals("Comment text", savedComment.getText());
        verify(itemRepository, times(1)).findById(1L);
        verify(userService, times(1)).getById(1L);
        verify(bookingService, times(1)).isUserBookedItem(1L, 1L);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void addComment_ShouldThrowException_WhenUserNotBookedItem() {
        Item item = new Item();
        item.setId(1L);

        User user = new User();
        user.setId(1L);

        Comment comment = new Comment();
        comment.setText("Comment text");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.getById(1L)).thenReturn(user);
        when(bookingService.isUserBookedItem(1L, 1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> itemService.addComment(1L, 1L, comment));
        verify(itemRepository, times(1)).findById(1L);
        verify(userService, times(1)).getById(1L);
        verify(bookingService, times(1)).isUserBookedItem(1L, 1L);
        verify(commentRepository, times(0)).save(any(Comment.class));
    }

    @Test
    void getById_ShouldReturnItem() {
        Item item = new Item();
        item.setId(1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item foundItem = itemService.getById(1L);

        assertNotNull(foundItem);
        assertEquals(1L, foundItem.getId());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void getById_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(1L));
        verify(itemRepository, times(1)).findById(1L);
    }
}
