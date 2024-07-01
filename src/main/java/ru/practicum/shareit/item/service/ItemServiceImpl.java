package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
        itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(Constants.ITEM_NOT_FOUND));
        if (!item.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException(Constants.USER_NOT_OWNER);
        }
        itemMapper.updateItemFromItemDto(itemDto, item);
        itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDtoResponse getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(Constants.ITEM_NOT_FOUND));
        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(itemId);
        item.setComments(comments);
        return setItemToItemDtoWithBookings(item, userId);
    }

    @Override
    public List<ItemDtoResponse> getAll(Long ownerId) {
        return itemRepository.findAllByOwnerIdOrderById(ownerId).stream()
                .map(item -> setItemToItemDtoWithBookings(item, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        if (text.isEmpty()) {
            log.info("Параметр 'text' пустой. Получен пустой список Item");
            return Collections.emptyList();
        }
        return itemRepository.findByNameIgnoreCaseContainingAndAvailableTrueOrDescriptionIgnoreCaseContainingAndAvailableTrue(text, text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private ItemDtoResponse setItemToItemDtoWithBookings(Item item, Long userId) {
        ItemDtoResponse itemDtoResponse = itemMapper.toItemDtoResponse(item);
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
            if (nextBooking != null) {
                itemDtoResponse.setNextBooking(new ItemDtoResponse.BookingDto(nextBooking.getId(), nextBooking.getBooker().getId()));
            }
            if (lastBooking != null) {
                itemDtoResponse.setLastBooking(new ItemDtoResponse.BookingDto(lastBooking.getId(), lastBooking.getBooker().getId()));
            }
        }
        return itemDtoResponse;
    }

    @Override
    public CommentDtoResponse addComment(Long userId, Long itemId, CommentDto commentDto) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndEndIsBefore(itemId, userId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BadRequestException(Constants.NO_COMMENT);
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(Constants.ITEM_NOT_FOUND));
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
        Comment comment = itemMapper.toComment(commentDto);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setAuthor(author);

        Comment commentWithId = commentRepository.save(comment);
        CommentDtoResponse commentDtoResponse = itemMapper.toCommentDtoResponse(commentWithId);
        commentDtoResponse.setAuthorName(author.getName());
        return commentDtoResponse;
    }
}