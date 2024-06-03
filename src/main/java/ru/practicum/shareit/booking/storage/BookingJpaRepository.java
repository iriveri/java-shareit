package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {
    Slice<Booking> findByBookerId(Specification<Booking> spec, Pageable pageable);

    Slice<Booking> findByItemOwnerId(Specification<Booking> spec, Pageable pageable);

    Slice<Booking> findByItemId(Specification<Booking> spec, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND " +
            "(b.start < :end AND b.end > :start)")
    List<Booking> findOverlappingBookings(Long itemId, LocalDateTime start, LocalDateTime end);

}