package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exceptions.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private Long bookerId;
    @NotNull(groups = {Create.class})
    private Long itemId;
    @NotNull(groups = {Create.class})
    private LocalDateTime start;
    @NotNull(groups = {Create.class})
    private LocalDateTime end;
    private State state;

    public BookingDto(Long id, Long bookerId, Long itemId, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.bookerId = bookerId;
        this.itemId = itemId;
        this.start = start;
        this.end = end;
    }

}
