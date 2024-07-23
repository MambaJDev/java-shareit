package ru.practicum.shareit.request.service;


import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDtoResponse saveRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
        ItemRequest itemRequest = requestRepository.save(
                itemRequestMapper.toItemRequest(itemRequestDto, user, LocalDateTime.now()));
        return itemRequestMapper.toItemRequestDtoResponse(itemRequest);
    }

    @Override
    public List<ItemRequestDtoResponse> getAllByRequesterId(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
        List<ItemRequest> itemRequests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return setUpItemRequestDtoResponse(itemRequests);
    }

    @Override
    public List<ItemRequestDtoResponse> getAllRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
        Sort sortByDate = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(from / size, size, sortByDate);
        List<ItemRequest> itemRequests = requestRepository.findAllStrangerRequests(userId, page);
        return setUpItemRequestDtoResponse(itemRequests);
    }

    @Override
    public ItemRequestDtoResponse getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(Constants.REQUEST_NOT_FOUND));
        ItemRequestDtoResponse itemRequestDtoResponse = itemRequestMapper.toItemRequestDtoResponse(itemRequest);
        itemRequestDtoResponse.setItems(itemRepository.findAllByRequestId(itemRequest.getId()));
        return itemRequestDtoResponse;
    }

    private List<ItemRequestDtoResponse> setUpItemRequestDtoResponse(List<ItemRequest> itemRequests) {
        List<ItemRequestDtoResponse> itemRequestDtoResponses = itemRequestMapper.toItemRequestDtoResponseList(itemRequests);
        itemRequestDtoResponses.forEach(itemRequest -> itemRequest.setItems(itemRepository.findAllByRequestId(itemRequest.getId())));
        return itemRequestDtoResponses;
    }
}
