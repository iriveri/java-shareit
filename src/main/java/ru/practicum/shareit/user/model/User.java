package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String name;
}