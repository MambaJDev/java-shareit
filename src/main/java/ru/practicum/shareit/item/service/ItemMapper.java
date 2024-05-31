package ru.practicum.shareit.item.service;


import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toItem(ItemShort itemShort);
}