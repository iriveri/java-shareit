package ru.practicum.gateway.item.service;

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
import ru.practicum.common.item.dto.CommentDto;
import ru.practicum.common.item.dto.ExtendedItemDto;
import ru.practicum.common.item.dto.ItemDto;

import java.util.List;

@Service
public class ItemService {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    @Autowired
    public ItemService(RestTemplate restTemplate, String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    @CacheEvict(value = {"items", "itemsByOwner", "itemsSearch", "userRequests", "allRequests", "requestsById"}, allEntries = true)
    public ItemDto create(ItemDto itemDto, Long userId) {
        String url = serverUrl + "/items";

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(itemDto, headers);
        ResponseEntity<ItemDto> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ItemDto.class);

        return responseEntity.getBody();
    }

    @CacheEvict(value = {"items", "itemsByOwner", "itemsSearch", "userRequests", "allRequests", "requestsById"}, allEntries = true)
    public ItemDto edit(Long itemId, ItemDto itemDto, Long userId) {
        String url = String.format(serverUrl + "/items/%d", itemId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(itemDto, headers);

        return restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, ItemDto.class).getBody();
    }

    @Cacheable(value = "items", key = "#itemId + ':' + #userId")
    public ExtendedItemDto getById(Long itemId, Long userId) {
        String url = String.format(serverUrl + "/items/%d", itemId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<ExtendedItemDto> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ExtendedItemDto.class);
        return responseEntity.getBody();
    }

    @Cacheable(value = "itemsByOwner", key = "#userId + ':' + #offset + ':' + #limit")
    public List<ExtendedItemDto> getItemsByOwner(Long userId, int offset, int limit) {
        String url = String.format(serverUrl + "/items?from=%d&size=%d", offset, limit);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<ExtendedItemDto>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<ExtendedItemDto>>() {
        });
        return responseEntity.getBody();
    }

    @Cacheable(value = "itemsSearch", key = "#text + ':' + #offset + ':' + #limit")
    public List<ItemDto> searchItemsByText(String text, int offset, int limit) {
        String url = String.format(serverUrl + "/items/search?text=%s&from=%d&size=%d", text, offset, limit);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<ItemDto>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<ItemDto>>() {
        });
        return responseEntity.getBody();
    }

    @CacheEvict(value = {"items", "itemsByOwner", "itemsSearch"}, allEntries = true)
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        String url = String.format(serverUrl + "/items/%d/comment", itemId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        HttpEntity<CommentDto> requestEntity = new HttpEntity<>(commentDto, headers);

        return restTemplate.postForObject(url, requestEntity, CommentDto.class);
    }
}
