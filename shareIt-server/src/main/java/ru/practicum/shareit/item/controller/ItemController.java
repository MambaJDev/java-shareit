package ru.practicum.shareit.item.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto save(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                        @RequestBody ItemDto itemDto) {
        log.info("Post-запрос на добавление Item");
        return itemService.save(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId) {
        log.info("Patch-запрос от User c ID = {} на обновление Item с ID = {}", userId, itemId);
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse getItemById(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                       @PathVariable Long itemId) {
        log.info("GET-запрос от User c ID = {} на получение Item с ID = {}", userId, itemId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoResponse> getAll(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                        @RequestParam Integer from,
                                        @RequestParam Integer size) {
        log.info("GET-запрос на получение всех Item Юзера c ID = {}", userId);
        return itemService.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                     @RequestParam String text,
                                     @RequestParam Integer from,
                                     @RequestParam Integer size) {
        log.info("GET-запрос от User c ID = {} на поиск Item ", userId);
        return itemService.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse addComment(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                         @PathVariable Long itemId,
                                         @RequestBody CommentDto commentDto) {
        log.info("Post-запрос addComment");
        return itemService.addComment(userId, itemId, commentDto);
    }
}