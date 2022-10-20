package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @Column(name = "item_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @Column(name = "item_name", nullable = false)
    private String name;
    @Column(length = 1024)
    private String description;
    @Column(nullable = false)
    private Boolean available;
    @OneToMany
    @JoinColumn(name = "item_id")
    private List<Comment> comments;


    public Item(Long id, User owner, String name, String description, Boolean available) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public Item() {
    }

    public Item(User owner, String name, String description, Boolean available) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
