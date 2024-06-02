package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    /**
     * Создание нового запроса на вещь
     * Endpoint: POST /requests
     * Принимает объект RequestDto в теле запроса.
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, создающего запрос.
     */
    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(@Valid @RequestBody ItemRequestDto requestDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemRequestDto createdRequest = itemRequestService.createRequest(userId, requestDto);
        log.info("Request for user with ID {} has been successfully created", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    /**
     * Просмотр списка запросов пользователя.
     * Endpoint: GET /requests
     * Возвращает список запросов пользователя вместе с данными об ответах на них.
     * Запросы должны возвращаться в отсортированном в порядке от более новых к более старым
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, запросившего список.
     */
    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemRequestDto> requests = itemRequestService.getUserRequests(userId);
        log.info("Requests for user with ID {} have been successfully fetched", userId);
        return ResponseEntity.status(HttpStatus.OK).body(requests);
    }

    /**
     * Просмотр списка запросов конкретного пользователя.
     * Endpoint: GET /requests/all?from={from}&size{size}
     * Возвращает список запросов созданных другими пользователями.
     * Запросы должны возвращаться в отсортированном в порядке от более новых к более старым
     * В запросе присутствует пагинация: from - индекс первого элемента, size - количество элементов для отображения
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, запросившего список.
     */
    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        List<ItemRequestDto> requests = itemRequestService.getAllRequests(userId, from, size);
        log.info("All requests for user with ID {} have been successfully fetched", userId);
        return ResponseEntity.status(HttpStatus.OK).body(requests);
    }

    /**
     * Просмотр данных об конкретном запросе.
     * Endpoint: GET /requests/{requestId}
     * Возвращает данные о запросе вместе с данными об ответах на него.
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, запросившего данные.
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        ItemRequestDto request = itemRequestService.getRequestById(userId, requestId);
        log.info("Request with ID {} for user with ID {} has been successfully fetched", requestId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(request);
    }
}
