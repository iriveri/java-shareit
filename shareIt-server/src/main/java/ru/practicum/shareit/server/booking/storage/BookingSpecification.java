package ru.practicum.shareit.server.booking.storage;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.common.booking.model.Booking;
import ru.practicum.shareit.common.booking.model.BookingStatus;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;

public class BookingSpecification {

    public static Specification<Booking> hasState(String state) {
        return (Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (state == null || state.equalsIgnoreCase("ALL")) {
                return criteriaBuilder.conjunction();
            }

            BookingStatus status = BookingStatus.valueOf(state.toUpperCase());
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Booking> isCurrent() {
        return (Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            LocalDateTime now = LocalDateTime.now();
            return criteriaBuilder.and(
                    criteriaBuilder.lessThan(root.get("start"), now),
                    criteriaBuilder.greaterThan(root.get("end"), now)
            );
        };
    }

    public static Specification<Booking> isPast() {
        return (Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            LocalDateTime now = LocalDateTime.now();
            return criteriaBuilder.lessThan(root.get("end"), now);
        };
    }

    public static Specification<Booking> isFuture() {
        return (Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            LocalDateTime now = LocalDateTime.now();
            return criteriaBuilder.greaterThan(root.get("start"), now);
        };
    }

    public static Specification<Booking> byOwnerId(Long ownerId) {
        return (Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("item").get("ownerId"), ownerId);
    }

    public static Specification<Booking> byBookerId(Long bookerId) {
        return (Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("booker").get("id"), bookerId);
    }

    public static Specification<Booking> byItemId(Long itemId) {
        return (Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("item").get("id"), itemId);
    }
}