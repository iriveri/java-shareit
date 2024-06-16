package ru.practicum.shareit.server.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestWithResponsesDto;

import java.util.List;

@Service
public class ItemRequestService {

    private final RestTemplate restTemplate;

    @Autowired
    public ItemRequestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "requests", key = "#userId + ':' + #requestDto.toString()")
    public ItemRequestDto create(Long userId, ItemRequestDto request) {
        String url = "http://main-application/requests";
        return restTemplate.postForObject(url, request, ItemRequestDto.class);
    }

    @Cacheable(value = "userRequests", key = "#userId")
    public List<ItemRequestWithResponsesDto> getUserRequests(Long userId) {
        String url = String.format("http://main-application/requests?userId=%d", userId);
        return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ItemRequestWithResponsesDto>>() {}).getBody();
    }

    @Cacheable(value = "allRequests", key = "#userId + ':' + #offset + ':' + #limit")
    public List<ItemRequestWithResponsesDto> getAllRequests(Long userId, int offset, int limit) {
        String url = String.format("http://main-application/requests/all?from=%d&size=%d", offset, limit);
        return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ItemRequestWithResponsesDto>>() {}).getBody();
    }

    @Cacheable(value = "requestsById", key = "#userId + ':' + #requestId")
    public ItemRequestWithResponsesDto getById(Long userId, Long requestId) {
        String url = String.format("http://main-application/requests/%d?userId=%d", requestId, userId);
        return restTemplate.getForObject(url, ItemRequestWithResponsesDto.class);
    }

}
