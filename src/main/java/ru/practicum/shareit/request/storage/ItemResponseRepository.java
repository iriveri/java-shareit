package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemResponse;

import java.util.List;

public interface ItemResponseRepository extends JpaRepository<ItemResponse, Long> {
    List<ItemResponse> findAllByRequest_Id(Long responseItemId);
}
