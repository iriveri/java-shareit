package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemIdOrderByStartDesc(Long itemId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND " +
            "(b.start < :end AND b.end > :start)")
    List<Booking> findOverlappingBookings(Long itemId, LocalDateTime start, LocalDateTime end);

}