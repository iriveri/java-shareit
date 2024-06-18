package ru.practicum.shareit.server.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.common.booking.dto.BookingRequestDto;
import ru.practicum.shareit.common.booking.dto.BookingResponseDto;


import java.util.List;

@Service
public class BookingService {

    private final RestTemplate restTemplate;

    @Autowired
    public BookingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "bookings", key = "#bookingDto.toString() + #bookerId")
    public BookingResponseDto createBooking(BookingRequestDto bookingDto, Long bookerId) {
        String url = "http://main-application/bookings"; // URL основного приложения
        return restTemplate.postForObject(url, bookingDto, BookingResponseDto.class);
    }

    @Cacheable(value = "bookingStatus", key = "#ownerId + ':' + #bookingId + ':' + #approved")
    public BookingResponseDto updateBookingStatus(Long ownerId, Long bookingId, boolean approved) {
        String url = String.format("http://main-application/bookings/%d?approved=%b", bookingId, approved);
        return restTemplate.patchForObject(url, null, BookingResponseDto.class);
    }

    @Cacheable(value = "bookings", key = "#userId + ':' + #bookingId")
    public BookingResponseDto getBooking(Long userId, Long bookingId) {
        String url = String.format("http://main-application/bookings/%d", bookingId);
        return restTemplate.getForObject(url, BookingResponseDto.class);
    }

    @Cacheable(value = "userBookings", key = "#userId + ':' + #state + ':' + #offset + ':' + #limit")
    public List<BookingResponseDto> getUserBookings(Long userId, String state, int offset, int limit) {
        String url = String.format("http://main-application/bookings?state=%s&from=%d&size=%d", state, offset, limit);
        return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<BookingResponseDto>>() {
        }).getBody();
    }

    @Cacheable(value = "ownerBookings", key = "#ownerId + ':' + #state + ':' + #offset + ':' + #limit")
    public List<BookingResponseDto> getOwnerBookings(Long ownerId, String state, int offset, int limit) {
        String url = String.format("http://main-application/bookings/owner?state=%s&from=%d&size=%d", state, offset, limit);
        return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<BookingResponseDto>>() {
        }).getBody();
    }
}
