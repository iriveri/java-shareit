package ru.practicum.gateway.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.common.item.dto.CommentDto;
import ru.practicum.common.item.dto.ItemDto;

import java.util.List;

@Service
public class ItemService {

    private final RestTemplate restTemplate;

    @Autowired
    public ItemService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "items", key = "#itemDto.toString() + #userId")
    public ItemDto create(ItemDto itemDto, Long userId) {
        String url = "http://main-application/items";
        return restTemplate.postForObject(url, itemDto, ItemDto.class);
    }

    @Cacheable(value = "items", key = "#itemId + ':' + #itemDto.toString() + #userId")
    public ItemDto edit(Long itemId, ItemDto itemDto, Long userId) {
        String url = String.format("http://main-application/items/%d", itemId);
        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(itemDto);
        return restTemplate.exchange(url, HttpMethod.PUT, requestEntity, ItemDto.class).getBody();
    }

    @Cacheable(value = "items", key = "#itemId + ':' + #userId")
    public ItemDto getById(Long itemId) {
        String url = String.format("http://main-application/items/%d", itemId);
        return restTemplate.getForObject(url, ItemDto.class);
    }

    @Cacheable(value = "itemsByOwner", key = "#userId + ':' + #offset + ':' + #limit")
    public List<ItemDto> getItemsByOwner(Long userId, int offset, int limit) {
        String url = String.format("http://main-application/items?from=%d&size=%d", offset, limit);
        return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ItemDto>>() {
        }).getBody();
    }

    @Cacheable(value = "itemsSearch", key = "#text + ':' + #offset + ':' + #limit")
    public List<ItemDto> searchItemsByText(String text, int offset, int limit) {
        String url = String.format("http://main-application/items/search?text=%s&from=%d&size=%d", text, offset, limit);
        return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ItemDto>>() {
        }).getBody();
    }

    @Cacheable(value = "comments", key = "#itemId + ':' + #commentDto.toString() + #userId")
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        String url = String.format("http://main-application/items/%d/comment", itemId);
        return restTemplate.postForObject(url, commentDto, CommentDto.class);
    }
}
