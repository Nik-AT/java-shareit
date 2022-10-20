package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.States;
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
    private States states;

}
