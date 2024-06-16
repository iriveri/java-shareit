package ru.practicum.shareit.server.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.util.List;

@Service
public class UserService {

    private final RestTemplate restTemplate;

    @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CacheEvict(value = "users", allEntries = true)
    public UserDto create(UserDto user) {
        String url = "http://main-application/users";
        return restTemplate.postForObject(url, user, UserDto.class);
    }

    @CacheEvict(value = "users", key = "#userId")
    public UserDto edit(Long userId, UserDto user) {
        String url = String.format("http://main-application/users/%d", userId);
        restTemplate.put(url, user);
        HttpEntity<UserDto> requestEntity = new HttpEntity<>(user);
        return restTemplate.exchange(url, HttpMethod.PUT, requestEntity, UserDto.class).getBody();
    }

    @Cacheable(value = "users", key = "#userId")
    public UserDto getById(Long userId) {
        String url = String.format("http://main-application/users/%d", userId);
        return restTemplate.getForObject(url, UserDto.class);
    }

    @CacheEvict(value = "users", key = "#userId")
    public void delete(Long userId) {
        String url = String.format("http://main-application/users/%d", userId);
        restTemplate.delete(url);
    }

    @Cacheable(value = "users")
    public List<UserDto> getAllUsers() {
        String url = "http://main-application/users";
        return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<UserDto>>() {
        }).getBody();
    }
}
