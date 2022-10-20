package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.States;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InfoBookingDto {

    private Long id;
    private User booker;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
    private States status;
}
