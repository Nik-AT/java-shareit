package ru.practicum.shareit.request;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.InfoItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final RequestService requestService;

    @Autowired
    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public InfoItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Создание запроса");
        return requestService.createRequest(itemRequestDto, userId);

    }

    @GetMapping
    public List<InfoItemRequestDto> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение списка запросов от пользователя {}", userId);
        return requestService.getRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<InfoItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(name = "from", defaultValue = "0")
                                                   Integer from,
                                                   @RequestParam(name = "size", defaultValue = "10")
                                                   Integer size) {
        log.info("Получение всех запросов");
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("creationTime").descending());
        return requestService.getAllRequests(userId, pageRequest);
    }

    @GetMapping("/{requestId}")
    public InfoItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long requestId) {
        log.info("Запрос на получение данных");
        return requestService.getRequestById(requestId, userId);
    }

}
