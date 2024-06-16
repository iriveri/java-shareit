package ru.practicum.shareit.server.request.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "item_request")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column(nullable = false)
    private LocalDateTime created;

    public ItemRequest(ItemRequest itemRequest) {
        this.id = itemRequest.getId();
        this.description = itemRequest.getDescription();
        this.requester = itemRequest.getRequester();
        this.created = itemRequest.getCreated();
    }

}
