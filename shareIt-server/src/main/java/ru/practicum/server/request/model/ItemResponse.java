package ru.practicum.server.request.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.server.item.model.Item;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "item_response")
public class ItemResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_item_id", nullable = false)
    private Item responseItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private ItemRequest request;

}