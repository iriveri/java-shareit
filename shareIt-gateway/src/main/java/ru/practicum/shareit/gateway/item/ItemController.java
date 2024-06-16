package ru.practicum.shareit.gateway.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.CommentMapper;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.item.dto.ItemMapper;
import ru.practicum.shareit.gateway.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
public class ItemController {
    private final ItemService service;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemController(ItemService service, CommentMapper commentMapper, ItemMapper itemMapper) {
        this.service = service;
        this.commentMapper = commentMapper;
        this.itemMapper = itemMapper;
    }

    /**
     * Добавление новой вещи.
     * Endpoint: POST /items
     * Этот метод принимает объект {@link ItemDto} в теле запроса.
     * Идентификатор пользователя, добавляющего вещь, передается в заголовке X-Sharer-User-Id.
     *
     * @param itemDto объект {@link ItemDto}, представляющий новую вещь
     * @param userId  идентификатор пользователя, добавляющего вещь, передается в заголовке X-Sharer-User-Id
     * @return {@link ResponseEntity} содержащий добавленный объект {@link ItemDto} и статус ответа {@link HttpStatus#CREATED}
     */
    @PostMapping
    public ResponseEntity<ItemDto> addItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        var item = itemMapper.toItem(itemDto);
        item = service.create(item, userId);
        log.info("New item is created with ID {}", item.getId());
        var itemToTransfer = itemMapper.toDto(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemToTransfer);
    }

    /**
     * Редактирование вещи.
     * Endpoint: PATCH /items/{itemId}
     * Позволяет изменить название, описание и статус доступа к аренде вещи.
     * Редактировать вещь может только её владелец.
     *
     * @param itemId  идентификатор редактируемой вещи
     * @param itemDto объект {@link ItemDto}, содержащий новые данные для вещи
     * @param userId  идентификатор пользователя, владельца вещи, передается в заголовке X-Sharer-User-Id
     * @return {@link ResponseEntity} содержащий обновленный объект {@link ItemDto} и статус ответа {@link HttpStatus#OK}
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> editItem(
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        var item = itemMapper.toItem(itemDto);
        item = service.edit(itemId, item, userId);
        log.info("Item data for ID {} has been successfully patched", itemId);
        var itemToTransfer = itemMapper.toDto(item);
        return ResponseEntity.status(HttpStatus.OK).body(itemToTransfer);
    }

    /**
     * Просмотр информации о конкретной вещи.
     * Endpoint: GET /items/{itemId}
     * Информацию о вещи может просмотреть любой пользователь.
     *
     * @param itemId идентификатор запрашиваемой вещи
     * @param userId идентификатор пользователя, просматривающего информацию, передается в заголовке X-Sharer-User-Id
     * @return {@link ResponseEntity} содержащий объект {@link ItemDto} с информацией о вещи и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        var item = service.getById(itemId);
        var itemWithBooking = service.getExtendedItem(item, userId);
        log.info("Item data for ID {} has been successfully extracted", item.getId());
        var itemToTransfer = itemMapper.toExtendedDto(itemWithBooking);
        return ResponseEntity.status(HttpStatus.OK).body(itemToTransfer);
    }

    /**
     * Просмотр списка всех вещей пользователя.
     * Endpoint: GET /items?from={from}&size={size}
     * Возвращает список всех вещей пользователя с указанием их названия и описания.
     * В запросе присутствует пагинация: from - индекс первого элемента, size - количество элементов для отображения.
     *
     * @param userId идентификатор пользователя, передается в заголовке X-Sharer-User-Id
     * @param offset смещение для постраничного вывода
     * @param limit  количество записей для постраничного вывода
     * @return {@link ResponseEntity} содержащий список объектов {@link ItemDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<Object> getAllItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int offset,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int limit
    ) {
        var items = service.getItemsByOwner(userId, offset, limit);
        var itemsWithBooking = items.stream()
                .map(item -> service.getExtendedItem(item, userId))
                .collect(Collectors.toList());
        log.info("List consisting of {} items has been successfully fetched", items.size());
        var itemsToTransfer = itemsWithBooking.stream().map(itemMapper::toExtendedDto).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(itemsToTransfer);
    }

    /**
     * Поиск вещей по тексту.
     * Endpoint: GET /items/search?text={text}&from={from}&size={size}
     * Пользователь передает в строке запроса текст, по которому осуществляется поиск вещей.
     * Возвращает только доступные для аренды вещи.
     * В запросе присутствует пагинация: from - индекс первого элемента, size - количество элементов для отображения.
     *
     * @param text   текст для поиска
     * @param offset смещение для постраничного вывода
     * @param limit  количество записей для постраничного вывода
     * @return {@link ResponseEntity} содержащий список объектов {@link ItemDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(
            @RequestParam String text,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int offset,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int limit
    ) {
        var items = service.searchItemsByText(text, offset, limit);
        log.info("List consisting of {} items has been successfully fetched", items.size());
        var itemsToTransfer = items.stream().map(itemMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(itemsToTransfer);
    }

    /**
     * Добавление комментария к вещи.
     * Endpoint: POST /items/{itemId}/comment
     * Этот метод принимает объект {@link CommentDto} в теле запроса.
     * Идентификатор пользователя, добавляющего комментарий, передается в заголовке X-Sharer-User-Id.
     *
     * @param itemId     идентификатор вещи, к которой добавляется комментарий
     * @param commentDto объект {@link CommentDto}, представляющий новый комментарий
     * @param userId     идентификатор пользователя, добавляющего комментарий, передается в заголовке X-Sharer-User-Id
     * @return {@link ResponseEntity} содержащий добавленный объект {@link CommentDto} и статус ответа {@link HttpStatus#OK}
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        var comment = commentMapper.toComment(commentDto);
        comment = service.addComment(itemId, userId, comment);
        log.info("Comment has been successfully added to item with ID {}", itemId);
        var commentToTransfer = commentMapper.toDto(comment);
        return ResponseEntity.status(HttpStatus.OK).body(commentToTransfer);
    }
}
