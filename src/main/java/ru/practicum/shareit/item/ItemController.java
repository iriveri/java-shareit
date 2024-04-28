package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

/**
 * Контроллер для управления вещами.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    /**
     * Добавление новой вещи.
     * <p>
     * Endpoint: POST /items
     * Принимает объект ItemDto в теле запроса.
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, добавляющего вещь.
     * Владелец вещи должен быть указан в заголовке запроса.
     */
    @PostMapping
    public void addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") String userId) {
        // TODO: Implement method
    }

    /**
     * Редактирование вещи.
     * <p>
     * Endpoint: PATCH /items/{itemId}
     * Позволяет изменить название, описание и статус доступа к аренде вещи.
     * Редактировать вещь может только её владелец.
     */
    @PatchMapping("/{itemId}")
    public void editItem(@PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        // TODO: Implement method
    }

    /**
     * Просмотр информации о конкретной вещи.
     * <p>
     * Endpoint: GET /items/{itemId}
     * Информацию о вещи может просмотреть любой пользователь.
     */
    @GetMapping("/{itemId}")
    public void getItem(@PathVariable Long itemId) {
        // TODO: Implement method
    }

    /**
     * Просмотр списка всех вещей пользователя.
     * <p>
     * Endpoint: GET /items
     * Возвращает список всех вещей пользователя с указанием их названия и описания.
     */
    @GetMapping
    public void getAllItems() {
        // TODO: Implement method
    }

    /**
     * Поиск вещей по тексту.
     * <p>
     * Endpoint: GET /items/search?text={text}
     * Пользователь передает в строке запроса текст, по которому осуществляется поиск вещей.
     * Возвращает только доступные для аренды вещи.
     */
    @GetMapping("/search")
    public void searchItemsByText(@RequestParam String text) {
        // TODO: Implement method
    }
}