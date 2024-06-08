package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.ExtendedItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemMapper itemMapper;

    @MockBean
    private CommentMapper commentMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addItem_ShouldReturnCreated() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item name");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);
        Item item = new Item();
        Mockito.when(itemMapper.toItem(itemDto)).thenReturn(item);
        Mockito.when(itemMapper.toDto(item)).thenReturn(itemDto);
        Mockito.when(itemService.create(item, 1L)).thenReturn(item);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void addItem_EmptyName_ShouldReturnBadRequest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("must not be empty"));
    }

    @Test
    void editItem_ShouldReturnOk() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated name");
        itemDto.setDescription("Updated description");
        itemDto.setAvailable(true);
        Item item = new Item();
        Mockito.when(itemMapper.toItem(itemDto)).thenReturn(item);
        Mockito.when(itemMapper.toDto(item)).thenReturn(itemDto);
        Mockito.when(itemService.edit(anyLong(), any(), anyLong())).thenReturn(item);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getItem_ShouldReturnOk() throws Exception {
        ItemDto itemDto = new ItemDto();
        Item item = new Item();
        ExtendedItem extendedItem = new ExtendedItem(item);
        ExtendedItemDto extendedItemDto = new ExtendedItemDto();
        Mockito.when(itemService.getById(anyLong())).thenReturn(item);
        Mockito.when(itemService.getExtendedItem(item, 1L)).thenReturn(extendedItem);
        Mockito.when(itemMapper.toExtendedDto(extendedItem)).thenReturn(extendedItemDto);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    void getAllItems_ShouldReturnOk() throws Exception {
        ItemDto itemDto = new ItemDto();
        Item item = new Item();
        ExtendedItem extendedItem = new ExtendedItem(item);
        ExtendedItemDto extendedItemDto = new ExtendedItemDto();
        Mockito.when(itemService.getItemsByOwner(anyLong(), anyInt(), anyInt())).thenReturn(Collections.singletonList(item));
        Mockito.when(itemService.getExtendedItem(any(), anyLong())).thenReturn(extendedItem);
        Mockito.when(itemMapper.toExtendedDto(extendedItem)).thenReturn(extendedItemDto);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void searchItemsByText_ShouldReturnOk() throws Exception {
        ItemDto itemDto = new ItemDto();
        Item item = new Item();
        Mockito.when(itemService.searchItemsByText(anyString(), anyInt(), anyInt())).thenReturn(Collections.singletonList(item));
        Mockito.when(itemMapper.toDto(item)).thenReturn(itemDto);

        mockMvc.perform(get("/items/search")
                        .param("text", "search text")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void addComment_ShouldReturnOk() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment text");
        Comment comment = new Comment();
        Mockito.when(commentMapper.toComment(commentDto)).thenReturn(comment);
        Mockito.when(commentMapper.toDto(comment)).thenReturn(commentDto);
        Mockito.when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(comment);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void addComment_EmptyText_ShouldReturnBadRequest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("");

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.text").value("must not be blank"));
    }
}
