package ru.practicum.shareit.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class ItemRequestDtoGateway {

    @NotBlank
    @NotNull
    private String description;
}
