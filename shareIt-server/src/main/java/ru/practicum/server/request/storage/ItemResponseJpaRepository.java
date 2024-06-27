package ru.practicum.server.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.request.model.ItemResponse;

import java.util.List;

public interface ItemResponseJpaRepository extends JpaRepository<ItemResponse, Long> {
    List<ItemResponse> findAllByRequest_Id(Long responseItemId);
}
