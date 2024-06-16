package ru.practicum.shareit.server.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
public class ItemController {

    private final ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ItemDto> addItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        ItemDto createdItem = service.create(itemDto, userId);
        log.info("New item is created with ID {}", createdItem.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> editItem(
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        ItemDto updatedItem = service.edit(itemId, itemDto, userId);
        log.info("Item data for ID {} has been successfully patched", itemId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        ItemDto item = service.getById(itemId);
        log.info("Item data for ID {} has been successfully extracted", item.getId());
        return ResponseEntity.status(HttpStatus.OK).body(item);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int offset,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int limit
    ) {
        List<ItemDto> items = service.getItemsByOwner(userId, offset, limit);
        log.info("List consisting of {} items has been successfully fetched", items.size());
        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItemsByText(
            @RequestParam String text,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int offset,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int limit
    ) {
        List<ItemDto> items = service.searchItemsByText(text, offset, limit);
        log.info("List consisting of {} items has been successfully fetched", items.size());
        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        CommentDto addedComment = service.addComment(itemId, userId, commentDto);
        log.info("Comment has been successfully added to item with ID {}", itemId);
        return ResponseEntity.status(HttpStatus.OK).body(addedComment);
    }
}
