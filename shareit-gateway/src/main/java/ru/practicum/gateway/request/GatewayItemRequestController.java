package ru.practicum.gateway.request;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.gateway.constant.Constants;
import ru.practicum.gateway.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class GatewayItemRequestController {

    @Autowired
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> saveRequest(@RequestHeader(Constants.HEADER_USER_ID) long userId,
                                              @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.saveRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequesterId(@RequestHeader(Constants.HEADER_USER_ID) long userId) {
        return itemRequestClient.getAllByRequesterId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(Constants.HEADER_USER_ID) long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(Constants.HEADER_USER_ID) long userId,
                                             @PathVariable Long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}