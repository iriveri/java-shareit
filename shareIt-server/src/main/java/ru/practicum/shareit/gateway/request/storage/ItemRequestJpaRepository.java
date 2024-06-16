package ru.practicum.shareit.gateway.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.gateway.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestJpaRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Long requesterId);


    Slice<ItemRequest> findByRequesterIdNot(Long userId, Pageable pageable);
}
