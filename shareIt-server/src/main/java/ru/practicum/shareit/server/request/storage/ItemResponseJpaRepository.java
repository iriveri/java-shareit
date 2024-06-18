package ru.practicum.shareit.server.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.request.model.ItemResponse;

import java.util.List;

public interface ItemResponseJpaRepository extends JpaRepository<ItemResponse, Long> {
    List<ItemResponse> findAllByRequest_Id(Long responseItemId);
}
