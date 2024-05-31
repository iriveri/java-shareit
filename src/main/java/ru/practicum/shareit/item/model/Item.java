package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "\"item\"")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "owner_id")
    Long ownerId;
    String name;
    String description;
    Boolean available;

    public Item(Item item) {
        this.id = item.getId();
        this.ownerId = item.getOwnerId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.available = item.getAvailable();
    }
}
