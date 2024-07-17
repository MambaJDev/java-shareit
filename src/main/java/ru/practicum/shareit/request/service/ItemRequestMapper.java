package ru.practicum.shareit.request.service;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mapping(ignore = true,target = "id")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester, LocalDateTime created);

    ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest);

    List<ItemRequestDtoResponse> toItemRequestDtoResponseList(List<ItemRequest> itemRequest);
}