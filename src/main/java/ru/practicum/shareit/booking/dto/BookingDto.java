package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor

public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;

    @Data
    public static class Item {
        private final Long id;
        private final String name;
        private final String description;
    }

    @Data
    public static class User {
        private final Long id;
        private final String name;
    }
}
