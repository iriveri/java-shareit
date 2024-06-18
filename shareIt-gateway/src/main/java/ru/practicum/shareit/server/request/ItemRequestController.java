package ru.practicum.shareit.server.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.request.dto.ItemRequestDto;
import ru.practicum.shareit.common.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.server.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/requests")
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestService requestService;

    @Autowired
    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(
            @Valid @RequestBody ItemRequestDto request,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        ItemRequestDto itemRequest = requestService.create(userId, request);
        log.info("Request for user with ID {} has been successfully created", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemRequest);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestWithResponsesDto>> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        List<ItemRequestWithResponsesDto> requests = requestService.getUserRequests(userId);
        log.info("Requests for user with ID {} have been successfully fetched", userId);
        return ResponseEntity.status(HttpStatus.OK).body(requests);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestWithResponsesDto>> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int offset,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int limit
    ) {
        List<ItemRequestWithResponsesDto> requests = requestService.getAllRequests(userId, offset, limit);
        log.info("All requests for user with ID {} have been successfully fetched", userId);
        return ResponseEntity.status(HttpStatus.OK).body(requests);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestWithResponsesDto> getRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId
    ) {
        ItemRequestWithResponsesDto request = requestService.getById(userId, requestId);
        log.info("Request with ID {} for user with ID {} has been successfully fetched", requestId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(request);
    }
}
