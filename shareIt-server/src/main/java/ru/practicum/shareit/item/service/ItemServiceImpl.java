package ru.practicum.shareit.item.service;


import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.error.BadRequestException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            item.setRequestId(itemDto.getRequestId());
        }
        Item itemFromRepository = itemRepository.save(item);
        return itemMapper.toItemDto(itemFromRepository);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(Constants.ITEM_NOT_FOUND));
        if (!item.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException(Constants.USER_NOT_OWNER);
        }
        itemMapper.updateItemFromItemDto(itemDto, item);
        Item itemFromRepository = itemRepository.save(item);
        return itemMapper.toItemDto(itemFromRepository);
    }

    @Override
    public ItemDtoResponse getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(Constants.ITEM_NOT_FOUND));
        return setItemToItemDtoWithCommentsAndBookings(item, userId);
    }

    @Override
    public List<ItemDtoResponse> getAll(Long ownerId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return itemRepository.findAllByOwnerIdOrderById(ownerId, page).stream()
                .map(item -> setItemToItemDtoWithCommentsAndBookings(item, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return itemRepository.findByNameIgnoreCaseContainingAndAvailableTrueOrDescriptionIgnoreCaseContainingAndAvailableTrue(text, text, page).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private ItemDtoResponse setItemToItemDtoWithCommentsAndBookings(Item item, Long userId) {
        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());
        ItemDtoResponse itemDtoResponse = itemMapper.toItemDtoResponse(item, comments);

        if (Objects.equals(item.getOwner().getId(), userId)) {
            List<Booking> bookings = bookingRepository.findALLByItem(item);
            Booking nextBooking = bookings.stream()
                    .sorted(Comparator.comparing(Booking::getStart))
                    .filter(booking -> booking.getStatus() != Status.REJECTED)
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .findFirst().orElse(null);
            Booking lastBooking = bookings.stream()
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .filter(booking -> booking.getStatus() != Status.REJECTED)
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                    .findFirst().orElse(null);

            itemDtoResponse = itemMapper.updateItemDtoResponseWithBookings(itemDtoResponse, nextBooking, lastBooking);
        }
        return itemDtoResponse;
    }

    @Override
    public CommentDtoResponse addComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(Constants.ITEM_NOT_FOUND));
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndEndIsBefore(itemId, userId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BadRequestException(Constants.NO_COMMENT);
        }
        Comment comment = commentRepository.save(
                itemMapper.toComment(commentDto, item, author, LocalDateTime.now()));
        return itemMapper.commentToCommentDtoResponse(comment);
    }
}