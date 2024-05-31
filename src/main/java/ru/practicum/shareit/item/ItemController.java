package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.ItemExtensionType;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService service;

    private final ItemMapper mapper;
    @Autowired
    public ItemController( @Qualifier("JpaItemService") ItemService service, ItemMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Добавление новой вещи.
     * <p>
     * Endpoint: POST /items
     * Принимает объект ItemDto в теле запроса.
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, добавляющего вещь.
     * Владелец вещи должен быть указан в заголовке запроса.
     */
    @PostMapping
    public ResponseEntity<ItemDto> addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        var item = mapper.dtoItemToItem(itemDto);
        item = service.create(item, userId);//
        log.info("New item is created with ID {}", 1);
        var itemToTransfer = mapper.itemToItemDto(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemToTransfer);
    }

    /**
     * Редактирование вещи.
     * <p>
     * Endpoint: PATCH /items/{itemId}
     * Позволяет изменить название, описание и статус доступа к аренде вещи.
     * Редактировать вещь может только её владелец.
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> editItem(@PathVariable Long itemId, @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        var item = mapper.dtoItemToItem(itemDto);
        item = service.edit(itemId, item, userId);
        log.info("Item data for ID {} has been successfully patched", itemId);
        var itemToTransfer = mapper.itemToItemDto(item);
        return ResponseEntity.status(HttpStatus.OK).body(itemToTransfer);
    }

    /**
     * Просмотр информации о конкретной вещи.
     * <p>
     * Endpoint: GET /items/{itemId}
     * Информацию о вещи может просмотреть любой пользователь.
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId) {
        var item = service.getItemById(itemId);
        log.info("Item data for ID {} has been successfully extracted", item.getId());
        var itemToTransfer = mapper.itemToItemDto(item);
        return ResponseEntity.status(HttpStatus.OK).body(itemToTransfer);
    }

    /**
     * Просмотр списка всех вещей пользователя.
     * <p>
     * Endpoint: GET /items
     * Возвращает список всех вещей пользователя с указанием их названия и описания.
     */
    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        var items = service.getItemsByOwner(userId);
        var itemsWithBooking = items.stream()
                .map(item -> service.getAdditionalItemInfo(item, ItemExtensionType.BOOKING_TIME))
                .collect(Collectors.toList());
        log.info("List consisting of {} item has been successfully fetched", items.size());
        var itemsToTransfer = items.stream().map(mapper::itemToItemDto).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(itemsToTransfer);
    }

    /**
     * Поиск вещей по тексту.
     * <p>
     * Endpoint: GET /items/search?text={text}
     * Пользователь передает в строке запроса текст, по которому осуществляется поиск вещей.
     * Возвращает только доступные для аренды вещи.
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestParam String text) {
        var items = service.searchItemsByText(text);
        log.info("List consisting of {} item has been successfully fetched", items.size());
        var itemsToTransfer = items.stream().map(mapper::itemToItemDto).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(itemsToTransfer);
    }
}