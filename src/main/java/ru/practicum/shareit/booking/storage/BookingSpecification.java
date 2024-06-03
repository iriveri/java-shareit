package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

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
}