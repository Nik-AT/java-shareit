package ru.practicum.shareit.request.dto;


import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InfoItemRequestDto {

    private Long id;

    private String description;
    private LocalDateTime created;
    private List<Item> items;

    public InfoItemRequestDto(Long id, String description, LocalDateTime creationTime, List<Item> items) {
        this.id = id;
        this.description = description;
        this.created = creationTime;
        this.items = items;
    }
}

