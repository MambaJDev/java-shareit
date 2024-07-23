package ru.practicum.shareit.request.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.RequestService;

@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDtoResponse saveRequest(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                              @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Post-запрос saveRequest");
        return requestService.saveRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> getAllByRequesterId(@RequestHeader(Constants.HEADER_USER_ID) Long ownerId) {
        log.info("Get-запрос getAllByOwnerId");
        return requestService.getAllByRequesterId(ownerId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getAllRequests(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                                       @RequestParam Integer from,
                                                       @RequestParam Integer size) {
        log.info("Get-запрос getAllRequests");
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getRequestById(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Get-запрос getRequestById");
        return requestService.getRequestById(userId, requestId);
    }
}