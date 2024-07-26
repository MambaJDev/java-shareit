package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertNull;

class ItemMapperTest {
    private final ItemMapper itemMapper = new ItemMapperImpl();
    private Item item;
    private ItemDto itemDto;
    private Comment comment;


    @BeforeEach
    void setUp() {
        User user = new User()
                .setId(1L)
                .setName("Petr")
                .setEmail("petrov@mail.com");
        item = new Item()
                .setId(1L)
                .setName("Кружка")
                .setOwner(user)
                .setAvailable(true)
                .setDescription("Цвет синий, материал металл")
                .setRequestId(1L);
        itemDto = new ItemDto()
                .setId(1L)
                .setName("Кружка")
                .setAvailable(true)
                .setDescription("Цвет синий, материал металл")
                .setRequestId(1L);
        comment = new Comment()
                .setId(1L)
                .setAuthor(user)
                .setItem(item)
                .setCreated(LocalDateTime.now())
                .setText("Отличная кружка");
    }

    @Test
    void mapItemDtoToItem() {
        Item mappedItem = itemMapper.toItem(itemDto);

        Assertions.assertEquals(mappedItem.getId(), itemDto.getId());
        Assertions.assertEquals(mappedItem.getName(), itemDto.getName());
        Assertions.assertEquals(mappedItem.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(mappedItem.getRequestId(), itemDto.getRequestId());
        Assertions.assertEquals(mappedItem.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void toItemDto() {
        ItemDto mappedItemDto = itemMapper.toItemDto(item);

        Assertions.assertEquals(mappedItemDto.getId(), item.getId());
        Assertions.assertEquals(mappedItemDto.getName(), item.getName());
        Assertions.assertEquals(mappedItemDto.getDescription(), item.getDescription());
        Assertions.assertEquals(mappedItemDto.getRequestId(), item.getRequestId());
        Assertions.assertEquals(mappedItemDto.getAvailable(), item.getAvailable());
    }

    @Test
    void toItemDtoResponse() {
        CommentDtoResponse commentDtoResponse = new CommentDtoResponse()
                .setId(comment.getId())
                .setCreated(comment.getCreated())
                .setAuthorName(comment.getAuthor().getName())
                .setText(comment.getText());

        ItemDtoResponse mappedItemDtoResponse = itemMapper.toItemDtoResponse(item, List.of(comment));

        Assertions.assertEquals(mappedItemDtoResponse.getId(), item.getId());
        Assertions.assertEquals(mappedItemDtoResponse.getName(), item.getName());
        Assertions.assertEquals(mappedItemDtoResponse.getDescription(), item.getDescription());
        Assertions.assertEquals(mappedItemDtoResponse.getComments(), List.of(commentDtoResponse));
        Assertions.assertEquals(mappedItemDtoResponse.getAvailable(), item.getAvailable());
    }

    @Test
    void testWhenCommentDtoIsNull() {
        Comment comment = itemMapper.toComment(null, null, null, null);
        assertNull(comment);
    }

    @Test
    void testUpdateItemToItemDtoResponseWhenNull() {
        ItemDtoResponse itemDtoResponse = itemMapper.toItemDtoResponse(null, null);
        assertNull(itemDtoResponse);
    }

    @Test
    void testUpdateItemDtoResponseWithBookingsWhenNull() {
        ItemDtoResponse itemDtoResponse = itemMapper.updateItemDtoResponseWithBookings(null, null, null);
        assertNull(itemDtoResponse);
    }

    @Test
    void updateItemToItemDtoWhenNull() {
        Item item = itemMapper.toItem(null);
        assertNull(item);
    }
}