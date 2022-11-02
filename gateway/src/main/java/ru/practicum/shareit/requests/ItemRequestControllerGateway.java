package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Slf4j
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestControllerGateway {

    private final RequestClient requestClient;

    @Autowired
    public ItemRequestControllerGateway(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid ItemRequestDtoGateway itemRequestDto) {
        log.info("Создание запроса");
        return requestClient.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение списка запросов от пользователя {}", userId);
        return requestClient.getRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                 Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10")
                                                 Integer size) {
        log.info("Получение всех запросов");
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable @Positive Long requestId) {
        log.info("Запрос на получение данных");
        return requestClient.getRequestById(requestId, userId);
    }

}
