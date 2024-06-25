package ru.practicum.shareit.server.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.item.dto.CommentDto;
import ru.practicum.shareit.common.item.dto.ExtendedItemDto;
import ru.practicum.shareit.common.item.dto.ItemDto;
import ru.practicum.shareit.server.item.ItemController;
import ru.practicum.shareit.server.item.mapper.CommentMapper;
import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.ExtendedItem;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.service.ItemService;

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

    private ItemDto itemDto;
    private Item item;
    private ExtendedItem extendedItem;
    private ExtendedItemDto extendedItemDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setName("Item name");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);

        item = new Item();
        item.setName("Item name");
        item.setDescription("Item description");
        item.setAvailable(true);

        extendedItem = new ExtendedItem(item);
        extendedItemDto = new ExtendedItemDto();
    }

    @Test
    void addItem_ShouldReturnCreated() throws Exception {
        Mockito.when(itemMapper.toItem(itemDto)).thenReturn(item);
        Mockito.when(itemMapper.toDto(item)).thenReturn(itemDto);
        Mockito.when(itemService.create(item, 1L)).thenReturn(item);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void editItem_ShouldReturnOk() throws Exception {
        itemDto.setName("Updated name");
        itemDto.setDescription("Updated description");
        itemDto.setAvailable(true);

        Mockito.when(itemMapper.toItem(itemDto)).thenReturn(item);
        Mockito.when(itemMapper.toDto(item)).thenReturn(itemDto);
        Mockito.when(itemService.edit(anyLong(), any(Item.class), anyLong())).thenReturn(item);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void getItem_ShouldReturnOk() throws Exception {
        Mockito.when(itemService.getById(anyLong())).thenReturn(item);
        Mockito.when(itemService.getExtendedItem(item, 1L)).thenReturn(extendedItem);
        Mockito.when(itemMapper.toExtendedDto(extendedItem)).thenReturn(extendedItemDto);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(extendedItemDto.getName()))
                .andExpect(jsonPath("$.description").value(extendedItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(extendedItemDto.getAvailable()));
    }

    @Test
    void getAllItems_ShouldReturnOk() throws Exception {
        Mockito.when(itemService.getItemsByOwner(anyLong(), anyInt(), anyInt())).thenReturn(Collections.singletonList(item));
        Mockito.when(itemService.getExtendedItem(any(Item.class), anyLong())).thenReturn(extendedItem);
        Mockito.when(itemMapper.toExtendedDto(extendedItem)).thenReturn(extendedItemDto);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value(extendedItemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(extendedItemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(extendedItemDto.getAvailable()));
    }

    @Test
    void searchItemsByText_ShouldReturnOk() throws Exception {
        Mockito.when(itemService.searchItemsByText(anyString(), anyInt(), anyInt())).thenReturn(Collections.singletonList(item));
        Mockito.when(itemMapper.toDto(item)).thenReturn(itemDto);

        mockMvc.perform(get("/items/search")
                        .param("text", "search text")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void addComment_ShouldReturnOk() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment text");
        Comment comment = new Comment();
        Mockito.when(commentMapper.toComment(commentDto)).thenReturn(comment);
        Mockito.when(commentMapper.toDto(comment)).thenReturn(commentDto);
        Mockito.when(itemService.addComment(anyLong(), anyLong(), any(Comment.class))).thenReturn(comment);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value(commentDto.getText()));
    }


}