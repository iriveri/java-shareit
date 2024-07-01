package ru.practicum.gateway.booking.service;

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
import ru.practicum.common.booking.dto.BookingRequestDto;
import ru.practicum.common.booking.dto.BookingResponseDto;

import java.util.List;

@Service
public class BookingService {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    @Autowired
    public BookingService(RestTemplate restTemplate, String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    @CacheEvict(value = {"bookings", "userBookings", "ownerBookings"}, allEntries = true)
    public BookingResponseDto createBooking(BookingRequestDto bookingDto, Long bookerId) {
        String url = serverUrl + "/bookings";

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", bookerId.toString());
        HttpEntity<BookingRequestDto> requestEntity = new HttpEntity<>(bookingDto, headers);

        ResponseEntity<BookingResponseDto> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, BookingResponseDto.class);
        return response.getBody();
    }

    @CacheEvict(value = {"bookings", "userBookings", "ownerBookings"}, allEntries = true)
    public BookingResponseDto updateBookingStatus(Long ownerId, Long bookingId, boolean approved) {
        String url = String.format(serverUrl + "/bookings/%d?approved=%b", bookingId, approved);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", ownerId.toString());
        HttpEntity<BookingRequestDto> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<BookingResponseDto> response = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, BookingResponseDto.class);
        return response.getBody();
    }

    @Cacheable(value = "bookings", key = "#userId + ':' + #bookingId")
    public BookingResponseDto getBooking(Long userId, Long bookingId) {
        String url = String.format(serverUrl + "/bookings/%d", bookingId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        HttpEntity<BookingRequestDto> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<BookingResponseDto> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, BookingResponseDto.class);
        return response.getBody();
    }

    @Cacheable(value = "userBookings", key = "#userId + ':' + #state + ':' + #offset + ':' + #limit")
    public List<BookingResponseDto> getUserBookings(Long userId, String state, int offset, int limit) {
        String url = String.format(serverUrl + "/bookings?state=%s&from=%d&size=%d", state, offset, limit);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        HttpEntity<BookingRequestDto> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<BookingResponseDto>> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<BookingResponseDto>>() {
        });
        return response.getBody();
    }

    @Cacheable(value = "ownerBookings", key = "#ownerId + ':' + #state + ':' + #offset + ':' + #limit")
    public List<BookingResponseDto> getOwnerBookings(Long ownerId, String state, int offset, int limit) {
        String url = String.format(serverUrl + "/bookings/owner?state=%s&from=%d&size=%d", state, offset, limit);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", ownerId.toString());
        HttpEntity<BookingRequestDto> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<BookingResponseDto>> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<BookingResponseDto>>() {
        });
        return response.getBody();
    }
}
