package ru.practicum.shareit.request;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFound;
import ru.practicum.shareit.request.dto.InfoItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserService userService) {
        this.requestRepository = requestRepository;
        this.userService = userService;
    }

    @Override
    public InfoItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = requestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, userId));
        return ItemRequestMapper.toInfoItemRequestDto(itemRequest);
    }

    @Override
    public List<InfoItemRequestDto> getRequestsByUserId(Long userId) {
        userService.getById(userId);
        return requestRepository.findAllByUserId(userId)
                .stream()
                .map(ItemRequestMapper::toInfoItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InfoItemRequestDto> getAllRequests(Long userId, PageRequest pageRequest) {
        userService.getById(userId);
        return requestRepository.findAllByUserIdNot(userId, pageRequest)
                .stream()
                .map(ItemRequestMapper::toInfoItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public InfoItemRequestDto getRequestById(Long requestId, Long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFound("Запрос не найден"));
        return ItemRequestMapper.toInfoItemRequestDto(itemRequest);
    }
}

