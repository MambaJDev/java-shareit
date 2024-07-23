package ru.practicum.shareit.item.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.hamcrest.Matchers;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(item.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(item.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(item.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(item.getAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.is(item.getRequestId())));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(item.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(item.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(item.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(item.getAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.is(item.getRequestId())));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(itemDtoResponse.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(itemDtoResponse.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemDtoResponse.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(itemDtoResponse.getAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nextBooking.id", Matchers.is(itemDtoResponse.getNextBooking().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastBooking.id", Matchers.is(itemDtoResponse.getLastBooking().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nextBooking.bookerId", Matchers.is(itemDtoResponse.getNextBooking().getBookerId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastBooking.bookerId", Matchers.is(itemDtoResponse.getLastBooking().getBookerId())));
    }

    @Test
    void getAll() throws Exception {
        ItemDtoResponse itemDtoResponse = generator.nextObject(ItemDtoResponse.class);
        List<ItemDtoResponse> itemDtoResponses = List.of(itemDtoResponse);
        when(itemService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(itemDtoResponses);

        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['id']", Matchers.is(itemDtoResponses.get(0).getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['description']", Matchers.is((itemDtoResponses.get(0).getDescription()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['available']", Matchers.is(itemDtoResponses.get(0).getAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['nextBooking']['id']", Matchers.is((itemDtoResponses.get(0).getNextBooking().getId()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['lastBooking']['id']", Matchers.is((itemDtoResponses.get(0).getLastBooking().getId()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['nextBooking']['bookerId']", Matchers.is((itemDtoResponses.get(0).getNextBooking().getBookerId()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['lastBooking']['bookerId']", Matchers.is((itemDtoResponses.get(0).getLastBooking().getBookerId()))));
    }

    @Test
    void searchItems() throws Exception {
        ItemDto itemDto = generator.nextObject(ItemDto.class);
        List<ItemDto> itemDtoList = List.of(itemDto);
        when(itemService.searchItems(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(itemDtoList);

        mvc.perform(get("/items/search")
                        .queryParam("text", "java")
                        .queryParam("from", "0")
                        .queryParam("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['id']", Matchers.is(itemDtoList.get(0).getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['description']", Matchers.is((itemDtoList.get(0).getDescription()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['available']", Matchers.is(itemDtoList.get(0).getAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]['name']", Matchers.is((itemDtoList.get(0).getName()))));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(commentDtoResponse.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorName", Matchers.is(commentDtoResponse.getAuthorName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text", Matchers.is(commentDtoResponse.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created", Matchers.is(commentDtoResponse.getCreated().toString())));
    }
}