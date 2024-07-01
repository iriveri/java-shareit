package ru.practicum.gateway.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.common.user.dto.UserDto;

import java.util.List;

@Service
public class UserService {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    @Autowired
    public UserService(RestTemplate restTemplate, String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }
    @CacheEvict(value = {"users", "allUsers"}, allEntries = true)
    public UserDto create(UserDto user) {
        String url = serverUrl + "/users";
        return restTemplate.postForObject(url, user, UserDto.class);
    }
    @CacheEvict(value = {"users", "allUsers"}, allEntries = true)
    public UserDto edit(Long userId, UserDto user) {
        String url = String.format(serverUrl + "/users/%d", userId);
        HttpEntity<UserDto> requestEntity = new HttpEntity<>(user);
        return restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, UserDto.class).getBody();
    }
    @Cacheable(value = "users", key= "#userId")
    public UserDto getById(Long userId) {
        String url = String.format(serverUrl + "/users/%d", userId);
        return restTemplate.getForObject(url, UserDto.class);
    }

    @CacheEvict(value = {"users", "allUsers"}, allEntries = true)
    public void delete(Long userId) {
        String url = String.format(serverUrl + "/users/%d", userId);
        restTemplate.delete(url);
    }
    @Cacheable(value = "allUsers")
    public List<UserDto> getAllUsers() {
        String url = serverUrl + "/users";
        return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<UserDto>>() {
        }).getBody();
    }
}
