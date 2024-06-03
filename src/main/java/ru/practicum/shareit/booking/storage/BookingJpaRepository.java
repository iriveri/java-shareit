package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingJpaRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.booker.id = :bookerId " +
            "AND b.end < :currentTime ")
    boolean isUserBookedItem(Long bookerId, Long itemId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.start = " +
            "(SELECT MAX(bb.start) FROM Booking bb " +
            "   WHERE bb.item.id = :itemId " +
            "   AND bb.status = 'APPROVED' " +
            "   AND bb.item.ownerId = :userId " +
            "   AND bb.start < :now)" +
            "AND b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.item.ownerId = :userId ")
    Optional<Booking> getLastBooking(Long itemId, Long userId, LocalDateTime now);
    @Query("SELECT b FROM Booking b " +
            "WHERE b.start = " +
            "(SELECT MIN(bb.start) FROM Booking bb " +
            "   WHERE  bb.item.id = :itemId " +
            "   AND bb.status = 'APPROVED' " +
            "   AND bb.item.ownerId = :userId " +
            "   AND bb.start > :now )" +
            "AND b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.item.ownerId = :userId ")
    Optional<Booking> getNextBooking(Long itemId, Long userId, LocalDateTime now);
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND (b.start < :end AND b.end > :start)")
    List<Booking> findOverlappingBookings(Long itemId, LocalDateTime start, LocalDateTime end);

}