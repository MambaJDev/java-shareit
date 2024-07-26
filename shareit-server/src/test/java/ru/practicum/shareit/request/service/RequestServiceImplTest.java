package ru.practicum.shareit.request.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    private RequestService requestService;
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private User requester;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        requestService = new RequestServiceImpl(
                requestRepository,
                itemRequestMapper,
                userRepository,
                itemRepository
        );
        requester = new User()
                .setId(1L)
                .setName("Petr")
                .setEmail("petrov@mail.com");
        itemRequest = new ItemRequest()
                .setId(1L)
                .setRequester(requester)
                .setDescription("Ищу кружку")
                .setCreated(LocalDateTime.now());
        itemRequestDto = new ItemRequestDto()
                .setDescription("Ищу кружку");
    }

    @Nested
    @DisplayName("---SAVE ITEM REQUEST TESTS---")
    class SaveRequestTest {
        @Test
        void saveRequestWhenNormalCase() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
            when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
            ItemRequestDtoResponse itemRequestDtoResponse = requestService.saveRequest(requester.getId(), itemRequestDto);

            assertNotNull(itemRequestDtoResponse);
            Assertions.assertEquals(itemRequestDtoResponse.getId(), itemRequest.getId());
            Assertions.assertEquals(itemRequestDtoResponse.getDescription(), itemRequest.getDescription());
            Assertions.assertEquals(itemRequestDtoResponse.getCreated(), itemRequest.getCreated());

            verify(requestRepository, times(1)).save(any(ItemRequest.class));
            verify(userRepository, times(1)).findById(anyLong());
        }

        @Test
        void saveRequestWhenUserNotFound() {
            NotFoundException exception =
                    assertThrows(NotFoundException.class, () -> requestService.saveRequest(requester.getId(), itemRequestDto));
            Assertions.assertEquals(exception.getMessage(), Constants.USER_NOT_FOUND);

            verify(userRepository, times(1)).findById(anyLong());
        }
    }

    @Nested
    @DisplayName("---GET ALL REQUEST TESTS---")
    class GetRequestTest {
        @Test
        void getAllByRequesterIdWhenNormalCase() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
            when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));
            List<ItemRequestDtoResponse> requests = requestService.getAllByRequesterId(requester.getId());

            assertFalse(requests.isEmpty());
            Assertions.assertEquals(requests.get(0).getId(), itemRequest.getId());
            Assertions.assertEquals(requests.get(0).getCreated(), itemRequest.getCreated());
            Assertions.assertEquals(requests.get(0).getDescription(), itemRequest.getDescription());

            verify(requestRepository, times(1)).findAllByRequesterIdOrderByCreatedDesc(anyLong());
            verify(userRepository, times(1)).findById(anyLong());
        }

        @Test
        void getAllByRequesterIdWhenUserNotFound() {
            NotFoundException exception =
                    assertThrows(NotFoundException.class, () -> requestService.getAllByRequesterId(requester.getId()));
            Assertions.assertEquals(exception.getMessage(), Constants.USER_NOT_FOUND);

            verify(userRepository, times(1)).findById(anyLong());
        }

        @Test
        void getAllRequestsWhenNormalCase() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
            when(requestRepository.findAllStrangerRequests(anyLong(), any(Pageable.class))).thenReturn(List.of(itemRequest));
            List<ItemRequestDtoResponse> itemRequestDtoResponses = requestService.getAllRequests(2L, 0, 1);

            assertFalse(itemRequestDtoResponses.isEmpty());
            Assertions.assertEquals(itemRequestDtoResponses.get(0).getId(), itemRequest.getId());
            Assertions.assertEquals(itemRequestDtoResponses.get(0).getCreated(), itemRequest.getCreated());
            Assertions.assertEquals(itemRequestDtoResponses.get(0).getDescription(), itemRequest.getDescription());

            verify(requestRepository, times(1)).findAllStrangerRequests(anyLong(), any(Pageable.class));
            verify(userRepository, times(1)).findById(anyLong());
        }
    }

    @Nested
    @DisplayName("---GET REQUEST BY ID TESTS---")
    class GetRequestByIdTest {
        @Test
        void getRequestByIdWhenNormalCase() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
            when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
            ItemRequestDtoResponse itemRequestDtoResponse = requestService.getRequestById(requester.getId(), itemRequest.getId());

            assertNotNull(itemRequestDtoResponse);
            Assertions.assertEquals(itemRequestDtoResponse.getId(), itemRequest.getId());
            Assertions.assertEquals(itemRequestDtoResponse.getCreated(), itemRequest.getCreated());
            Assertions.assertEquals(itemRequestDtoResponse.getDescription(), itemRequest.getDescription());

            verify(requestRepository, times(1)).findById(anyLong());
            verify(userRepository, times(1)).findById(anyLong());
            verify(itemRepository, times(1)).findAllByRequestId(anyLong());
        }

        @Test
        void getRequestByIdWhenRequesterNotFound() {
            NotFoundException exception =
                    assertThrows(NotFoundException.class, () -> requestService.getRequestById(2L, 2L));
            Assertions.assertEquals(exception.getMessage(), Constants.USER_NOT_FOUND);

            verify(userRepository, times(1)).findById(anyLong());
        }

        @Test
        void getRequestByIdWhenRequestNotFound() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
            NotFoundException exception =
                    assertThrows(NotFoundException.class, () -> requestService.getRequestById(2L, 2L));
            Assertions.assertEquals(exception.getMessage(), Constants.REQUEST_NOT_FOUND);

            verify(requestRepository, times(1)).findById(anyLong());
        }
    }
}