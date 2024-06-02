package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDataDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestService requestService;
    private final ItemRequestMapper requestMapper;

    @Autowired
    public ItemRequestController(ItemRequestService requestService, ItemRequestMapper requestMapper) {
        this.requestService = requestService;
        this.requestMapper = requestMapper;
    }

    /**
     * Создание нового запроса на вещь
     * Endpoint: POST /requests
     * Принимает объект RequestDto в теле запроса.
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, создающего запрос.
     */
    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(
            @Valid @RequestBody ItemRequestDto requestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        ItemRequest itemRequest = requestMapper.toItemRequest(requestDto);
        itemRequest = requestService.createRequest(userId, itemRequest);
        log.info("Request for user with ID {} has been successfully created", userId);
        ItemRequestDto createdRequestDto = requestMapper.toItemRequestDto(itemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequestDto);
    }

    /**
     * Просмотр списка запросов пользователя.
     * Endpoint: GET /requests
     * Возвращает список запросов пользователя вместе с данными об ответах на них.
     * Запросы должны возвращаться в отсортированном в порядке от более новых к более старым
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, запросившего список.
     */
    @GetMapping
    public ResponseEntity<List<ItemRequestDataDto>> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        List<ItemRequest> requests = requestService.getUserRequests(userId);
        log.info("Requests for user with ID {} have been successfully fetched", userId);
        List<ItemRequestDataDto> requestDtos = requests.stream().map(requestMapper::toItemRequestDataDto).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(requestDtos);
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
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int offset,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) int limit
    ) {
        List<ItemRequest> requests = requestService.getAllRequests(userId, offset, limit);
        log.info("All requests for user with ID {} have been successfully fetched", userId);
        List<ItemRequestDto> requestDtos = requests
                .stream()
                .map(requestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(requestDtos);
    }

    /**
     * Просмотр данных об конкретном запросе.
     * Endpoint: GET /requests/{requestId}
     * Возвращает данные о запросе вместе с данными об ответах на него.
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, запросившего данные.
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDataDto> getRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId
    ) {
        ItemRequest request = requestService.getRequestById(userId, requestId);
        log.info("Request with ID {} for user with ID {} has been successfully fetched", requestId, userId);
        ItemRequestDataDto requestDto = requestMapper.toItemRequestDataDto(request);
        return ResponseEntity.status(HttpStatus.OK).body(requestDto);
    }
}