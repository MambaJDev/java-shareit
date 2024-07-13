package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.RequestService;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private RequestService requestService;
    @Autowired
    private MockMvc mvc;
    private final EasyRandom generator = new EasyRandom();

    @Test
    void saveRequest() throws Exception {
        ItemRequestDto itemRequestDto = generator.nextObject(ItemRequestDto.class);
        ItemRequestDtoResponse itemRequestDtoResponse = generator.nextObject(ItemRequestDtoResponse.class);
        when(requestService.saveRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(itemRequestDtoResponse);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoResponse.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDtoResponse.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDtoResponse.getCreated().toString())))
                .andExpect(jsonPath("$..items[*].id", is((itemRequestDtoResponse.getItems().stream()
                        .map(Item::getId).collect(Collectors.toList())))))
                .andExpect(jsonPath("$..items[*].name", is((itemRequestDtoResponse.getItems().stream()
                        .map(Item::getName).collect(Collectors.toList())))));
    }

    @Test
    void saveRequestWhenError() throws Exception {
        ItemRequestDto itemRequestDto = generator.nextObject(ItemRequestDto.class);
        itemRequestDto.setDescription(null);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value(containsString("must not be null")));
    }

    @Test
    void getAllByRequesterId() throws Exception {
        ItemRequestDtoResponse itemRequestDtoResponse = generator.nextObject(ItemRequestDtoResponse.class);
        List<ItemRequestDtoResponse> itemRequestDtoResponseList = List.of(itemRequestDtoResponse);
        when(requestService.getAllByRequesterId(anyLong())).thenReturn(itemRequestDtoResponseList);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]['id']", is(itemRequestDtoResponseList.get(0).getId())))
                .andExpect(jsonPath("$.[0]['created']", is((itemRequestDtoResponseList.get(0).getCreated().toString()))))
                .andExpect(jsonPath("$.[0]['description']", is((itemRequestDtoResponseList.get(0).getDescription()))))
                .andExpect(jsonPath("$..items[*].id", is((itemRequestDtoResponseList.get(0).getItems().stream()
                        .map(Item::getId).collect(Collectors.toList())))))
                .andExpect(jsonPath("$..items[*].name", is((itemRequestDtoResponseList.get(0).getItems().stream()
                        .map(Item::getName).collect(Collectors.toList())))));
    }

    @Test
    void getAllRequests() throws Exception {
        ItemRequestDtoResponse itemRequestDtoResponse = generator.nextObject(ItemRequestDtoResponse.class);
        List<ItemRequestDtoResponse> itemRequestDtoResponseList = List.of(itemRequestDtoResponse);
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(itemRequestDtoResponseList);

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]['id']", is(itemRequestDtoResponseList.get(0).getId())))
                .andExpect(jsonPath("$.[0]['created']", is((itemRequestDtoResponseList.get(0).getCreated().toString()))))
                .andExpect(jsonPath("$.[0]['description']", is((itemRequestDtoResponseList.get(0).getDescription()))))
                .andExpect(jsonPath("$..items[*].id", is((itemRequestDtoResponseList.get(0).getItems().stream()
                        .map(Item::getId).collect(Collectors.toList())))))
                .andExpect(jsonPath("$..items[*].name", is((itemRequestDtoResponseList.get(0).getItems().stream()
                        .map(Item::getName).collect(Collectors.toList())))));
    }

    @Test
    void getRequestById() throws Exception {
        ItemRequestDtoResponse itemRequestDtoResponse = generator.nextObject(ItemRequestDtoResponse.class);
        when(requestService.getRequestById(anyLong(), anyLong())).thenReturn(itemRequestDtoResponse);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoResponse.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDtoResponse.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDtoResponse.getCreated().toString())))
                .andExpect(jsonPath("$..items[*].id", is((itemRequestDtoResponse.getItems().stream()
                        .map(Item::getId).collect(Collectors.toList())))))
                .andExpect(jsonPath("$..items[*].name", is((itemRequestDtoResponse.getItems().stream()
                        .map(Item::getName).collect(Collectors.toList())))));
    }
}