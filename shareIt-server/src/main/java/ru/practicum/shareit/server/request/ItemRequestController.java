package ru.practicum.shareit.server.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.request.dto.ItemRequestDto;
import ru.practicum.shareit.common.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.server.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.service.ItemRequestService;

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
     * Создание нового запроса на вещь.
     * Endpoint: POST /requests
     * Принимает объект {@link ItemRequestDto} в теле запроса.
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, создающего запрос.
     *
     * @param requestDto объект {@link ItemRequestDto}, представляющий новый запрос
     * @param userId     идентификатор пользователя, создающего запрос, передается в заголовке X-Sharer-User-Id
     * @return {@link ResponseEntity} содержащий созданный объект {@link ItemRequestDto} и статус ответа {@link HttpStatus#CREATED}
     */
    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(
            @RequestBody ItemRequestDto requestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        ItemRequest itemRequest = requestMapper.toItemRequest(requestDto);
        itemRequest = requestService.create(userId, itemRequest);
        log.info("Request for user with ID {} has been successfully created", userId);
        ItemRequestDto createdRequestDto = requestMapper.toDto(itemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequestDto);
    }

    /**
     * Просмотр списка запросов пользователя.
     * Endpoint: GET /requests
     * Возвращает список запросов пользователя вместе с данными об ответах на них.
     * Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, запросившего список.
     *
     * @param userId идентификатор пользователя, запросившего список, передается в заголовке X-Sharer-User-Id
     * @return {@link ResponseEntity} содержащий список объектов {@link ItemRequestWithResponsesDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<List<ItemRequestWithResponsesDto>> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        List<ItemRequest> requests = requestService.getUserRequests(userId);
        log.info("Requests for user with ID {} have been successfully fetched", userId);
        List<ItemRequestWithResponsesDto> requestDtos = requests
                .stream()
                .map(requestService::getExtendedRequest)
                .map(requestMapper::toWithResponsesDto)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(requestDtos);
    }

    /**
     * Просмотр списка запросов, созданных другими пользователями.
     * Endpoint: GET /requests/all?from={from}&size{size}
     * Возвращает список запросов, созданных другими пользователями.
     * Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
     * В запросе присутствует пагинация: from - индекс первого элемента, size - количество элементов для отображения.
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, запросившего список.
     *
     * @param userId идентификатор пользователя, запросившего список, передается в заголовке X-Sharer-User-Id
     * @param offset смещение для постраничного вывода
     * @param limit  количество записей для постраничного вывода
     * @return {@link ResponseEntity} содержащий список объектов {@link ItemRequestWithResponsesDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestWithResponsesDto>> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0") int offset,
            @RequestParam(value = "size", defaultValue = "10") int limit
    ) {
        List<ItemRequest> requests = requestService.getAllRequests(userId, offset, limit);
        log.info("All requests for user with ID {} have been successfully fetched", userId);
        List<ItemRequestWithResponsesDto> requestDtos = requests
                .stream()
                .map(requestService::getExtendedRequest)
                .map(requestMapper::toWithResponsesDto)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(requestDtos);
    }

    /**
     * Просмотр данных о конкретном запросе.
     * Endpoint: GET /requests/{requestId}
     * Возвращает данные о запросе вместе с данными об ответах на него.
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, запросившего данные.
     *
     * @param userId    идентификатор пользователя, запросившего данные, передается в заголовке X-Sharer-User-Id
     * @param requestId идентификатор запрашиваемого запроса
     * @return {@link ResponseEntity} содержащий объект {@link ItemRequestWithResponsesDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestWithResponsesDto> getRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId
    ) {
        ItemRequest request = requestService.getById(userId, requestId);
        log.info("Request with ID {} for user with ID {} has been successfully fetched", requestId, userId);
        var extendedRequest = requestService.getExtendedRequest(request);
        ItemRequestWithResponsesDto requestDto = requestMapper.toWithResponsesDto(extendedRequest);
        return ResponseEntity.status(HttpStatus.OK).body(requestDto);
    }
}
