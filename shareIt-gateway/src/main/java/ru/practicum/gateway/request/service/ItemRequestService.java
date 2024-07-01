package ru.practicum.gateway.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.common.request.dto.ItemRequestDto;
import ru.practicum.common.request.dto.ItemRequestWithResponsesDto;

import java.util.List;

@Service
public class ItemRequestService {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    @Autowired
    public ItemRequestService(RestTemplate restTemplate, String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    @CacheEvict(value = {"userRequests", "allRequests", "requestsById"}, allEntries = true)
    public ItemRequestDto create(Long userId, ItemRequestDto request) {
        String url = serverUrl + "/requests";

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        HttpEntity<ItemRequestDto> requestEntity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(url, requestEntity, ItemRequestDto.class);
    }

    @Cacheable(value = "userRequests", key = "#userId")
    public List<ItemRequestWithResponsesDto> getUserRequests(Long userId) {
        String url = serverUrl + "/requests";

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        HttpEntity<ItemRequestDto> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<ItemRequestWithResponsesDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<ItemRequestWithResponsesDto>>() {
                });

        return response.getBody();
    }

    @Cacheable(value = "allRequests", key = "#userId + ':' + #offset + ':' + #limit")
    public List<ItemRequestWithResponsesDto> getAllRequests(Long userId, int offset, int limit) {
        String url = String.format(serverUrl + "/requests/all?from=%d&size=%d", offset, limit);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        HttpEntity<ItemRequestDto> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<ItemRequestWithResponsesDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<ItemRequestWithResponsesDto>>() {
                });

        return response.getBody();
    }

    @Cacheable(value = "requestsById", key = "#userId + ':' + #requestId")
    public ItemRequestWithResponsesDto getById(Long userId, Long requestId) {
        String url = String.format(serverUrl + "/requests/%d", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        HttpEntity<ItemRequestDto> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<ItemRequestWithResponsesDto> response = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, ItemRequestWithResponsesDto.class);

        return response.getBody();
    }

}
