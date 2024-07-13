package ru.practicum.shareit.item.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;
    private final EasyRandom generator = new EasyRandom();

    @Test
    void saveItem() throws Exception {
        ItemDto item = generator.nextObject(ItemDto.class);
        when(itemService.save(anyLong(), any(ItemDto.class))).thenReturn(item);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId())))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId())));
    }

    @Test
    void saveItemWhenError() throws Exception {
        ItemDto item = generator.nextObject(ItemDto.class);
        item.setName(null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value(containsString("must not be blank")));
    }

    @Test
    void update() throws Exception {
        ItemDto item = generator.nextObject(ItemDto.class);
        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(item);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId())))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId())));
    }

    @Test
    void getItemById() throws Exception {
        ItemDtoResponse itemDtoResponse = generator.nextObject(ItemDtoResponse.class);
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDtoResponse);

        mvc.perform(get("/items/{itemId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResponse.getId())))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.nextBooking.id", is(itemDtoResponse.getNextBooking().getId())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDtoResponse.getLastBooking().getId())))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemDtoResponse.getNextBooking().getBookerId())))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemDtoResponse.getLastBooking().getBookerId())));
    }

    @Test
    void getAll() throws Exception {
        ItemDtoResponse itemDtoResponse = generator.nextObject(ItemDtoResponse.class);
        List<ItemDtoResponse> itemDtoResponses = List.of(itemDtoResponse);
        when(itemService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(itemDtoResponses);

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]['id']", is(itemDtoResponses.get(0).getId())))
                .andExpect(jsonPath("$.[0]['description']", is((itemDtoResponses.get(0).getDescription()))))
                .andExpect(jsonPath("$.[0]['available']", is(itemDtoResponses.get(0).getAvailable())))
                .andExpect(jsonPath("$.[0]['nextBooking']['id']", is((itemDtoResponses.get(0).getNextBooking().getId()))))
                .andExpect(jsonPath("$.[0]['lastBooking']['id']", is((itemDtoResponses.get(0).getLastBooking().getId()))))
                .andExpect(jsonPath("$.[0]['nextBooking']['bookerId']", is((itemDtoResponses.get(0).getNextBooking().getBookerId()))))
                .andExpect(jsonPath("$.[0]['lastBooking']['bookerId']", is((itemDtoResponses.get(0).getLastBooking().getBookerId()))));
    }

    @Test
    void searchItems() throws Exception {
        ItemDto itemDto = generator.nextObject(ItemDto.class);
        List<ItemDto> itemDtoList = List.of(itemDto);
        when(itemService.searchItems(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(itemDtoList);

        mvc.perform(get("/items/search")
                        .queryParam("text", "sscs")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]['id']", is(itemDtoList.get(0).getId())))
                .andExpect(jsonPath("$.[0]['description']", is((itemDtoList.get(0).getDescription()))))
                .andExpect(jsonPath("$.[0]['available']", is(itemDtoList.get(0).getAvailable())))
                .andExpect(jsonPath("$.[0]['name']", is((itemDtoList.get(0).getName()))));
    }

    @Test
    void addComment() throws Exception {
        CommentDto commentDto = generator.nextObject(CommentDto.class);
        CommentDtoResponse commentDtoResponse = generator.nextObject(CommentDtoResponse.class);
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDtoResponse);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoResponse.getId())))
                .andExpect(jsonPath("$.authorName", is(commentDtoResponse.getAuthorName())))
                .andExpect(jsonPath("$.text", is(commentDtoResponse.getText())))
                .andExpect(jsonPath("$.created", is(commentDtoResponse.getCreated().toString())));
    }

    @Test
    void addCommentWhenError() throws Exception {
        CommentDto commentDto = generator.nextObject(CommentDto.class);
        commentDto.setText(" ");

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value(containsString("must not be blank")));
    }
}