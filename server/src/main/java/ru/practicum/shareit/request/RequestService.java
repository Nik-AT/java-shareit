package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.InfoItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    InfoItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId);

    List<InfoItemRequestDto> getRequestsByUserId(Long userId);

    List<InfoItemRequestDto> getAllRequests(Long userId, PageRequest pageRequest);

    InfoItemRequestDto getRequestById(Long requestId, Long userId);
}
