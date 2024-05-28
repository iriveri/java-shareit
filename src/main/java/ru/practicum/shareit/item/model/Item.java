package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Entity
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
}
