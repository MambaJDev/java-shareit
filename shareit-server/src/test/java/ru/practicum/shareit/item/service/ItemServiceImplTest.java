package ru.practicum.shareit.item.service;


import java.time.LocalDateTime;
import java.util.Collections;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private ItemService itemService;
    private final ItemMapper itemMapper = new ItemMapperImpl();
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    private User user;
    private User owner;
    private Item item;
    private ItemDto itemDto;
    private Booking booking1;
    private Booking booking2;
    private CommentDto commentDto;


    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(
                itemRepository, itemMapper, userRepository, bookingRepository, commentRepository);
        user = new User()
                .setId(1L)
                .setName("Petr")
                .setEmail("petrov@mail.com");
        owner = new User()
                .setId(2L)
                .setName("Ivan")
                .setEmail("ivanov@mail.com");
        item = new Item()
                .setId(1L)
                .setName("Кружка")
                .setOwner(owner)
                .setAvailable(true)
                .setDescription("Цвет синий, материал металл")
                .setRequestId(1L);
        itemDto = new ItemDto()
                .setId(1L)
                .setName("Кружка")
                .setAvailable(true)
                .setDescription("Цвет синий, материал металл")
                .setRequestId(1L);
        booking1 = new Booking()
                .setId(1L)
                .setItem(item)
                .setBooker(user)
                .setStatus(Status.APPROVED)
                .setStart(LocalDateTime.now().minusDays(2))
                .setEnd(LocalDateTime.now().minusDays(1));
        booking2 = new Booking()
                .setId(2L)
                .setItem(item)
                .setBooker(user)
                .setStatus(Status.WAITING)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(2));
        commentDto = new CommentDto().setText("Отличная кружка");
    }

    @Nested
    @DisplayName("---SAVE ITEM TESTS---")
    class SaveItemTest {
        @Test
        void saveItemNormalCase() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(itemRepository.save(any(Item.class))).thenReturn(item);

            ItemDto actualItemDto = itemService.save(user.getId(), itemDto);

            assertNotNull(actualItemDto);
            Assertions.assertEquals(actualItemDto.getId(), itemDto.getId());
            Assertions.assertEquals(actualItemDto.getName(), itemDto.getName());
            Assertions.assertEquals(actualItemDto.getDescription(), itemDto.getDescription());
            Assertions.assertEquals(actualItemDto.getRequestId(), itemDto.getRequestId());
        }

        @Test
        void saveItemWhenItemDtoEqualNull() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

            assertThrows(
                    NullPointerException.class, () -> itemService.save(user.getId(), null));
        }

        @Test
        void saveItemIfNoUser() {
            NotFoundException exception =
                    assertThrows(NotFoundException.class, () -> itemService.save(user.getId(), itemDto));
            Assertions.assertEquals(exception.getMessage(), Constants.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("---UPDATE ITEM TESTS---")
    class UpdateItemTest {
        @Test
        void updateItemNormalCase() {
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            when(itemRepository.save(any(Item.class))).thenReturn(item);

            ItemDto actualItemDto = itemService.update(owner.getId(), item.getId(), itemDto);

            assertNotNull(actualItemDto);
            Assertions.assertEquals(actualItemDto.getId(), itemDto.getId());
            Assertions.assertEquals(actualItemDto.getName(), itemDto.getName());
            Assertions.assertEquals(actualItemDto.getDescription(), itemDto.getDescription());
        }

        @Test
        void updateItemWhenItemDtoNameIsNull() {
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            when(itemRepository.save(any(Item.class))).thenReturn(item);
            itemDto.setName(null);

            ItemDto actualItemDto = itemService.update(owner.getId(), item.getId(), itemDto);

            assertNotNull(actualItemDto);
            Assertions.assertEquals(actualItemDto.getId(), itemDto.getId());
            Assertions.assertEquals(actualItemDto.getName(), item.getName());
            Assertions.assertEquals(actualItemDto.getDescription(), itemDto.getDescription());
        }

        @Test
        void updateItemIfItemNotFound() {
            NotFoundException exception =
                    assertThrows(NotFoundException.class, () -> itemService.update(user.getId(), item.getId(), itemDto));
            Assertions.assertEquals(exception.getMessage(), Constants.ITEM_NOT_FOUND);
        }

        @Test
        void updateItemIfUserNotOwner() {
            User owner = new User()
                    .setId(2L)
                    .setName("Sergo")
                    .setEmail("Sergo@mail.com");

            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            item.setOwner(owner);
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> itemService.update(user.getId(), item.getId(), itemDto));
            Assertions.assertEquals(exception.getMessage(), Constants.USER_NOT_OWNER);
        }
    }

    @Nested
    @DisplayName("---GET AND SEARCH ITEMS TESTS---")
    class GetAndSearchItemTest {
        @Test
        void getItemByIdWhenUserNotOwner() {
            ItemDtoResponse itemDtoResponse = new ItemDtoResponse()
                    .setId(1L)
                    .setName("Кружка")
                    .setAvailable(true)
                    .setDescription("Цвет синий, материал металл");
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            ItemDtoResponse actualItem = itemService.getItemById(user.getId(), item.getId());

            assertNotNull(actualItem);
            Assertions.assertEquals(actualItem.getId(), itemDtoResponse.getId());
            Assertions.assertEquals(actualItem.getName(), itemDtoResponse.getName());
            Assertions.assertEquals(actualItem.getDescription(), itemDtoResponse.getDescription());
            Assertions.assertTrue(actualItem.getComments().isEmpty());
            assertNull(actualItem.getLastBooking());
            assertNull(actualItem.getNextBooking());
        }

        @Test
        void getItemByIdWhenUserIsOwner() {
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            when(bookingRepository.findALLByItem(any(Item.class))).thenReturn(List.of(booking1, booking2));

            ItemDtoResponse actualItem = itemService.getItemById(owner.getId(), item.getId());

            assertNotNull(actualItem);
            Assertions.assertEquals(actualItem.getId(), item.getId());
            Assertions.assertEquals(actualItem.getName(), item.getName());
            Assertions.assertEquals(actualItem.getDescription(), item.getDescription());

            Assertions.assertEquals(actualItem.getLastBooking(), itemMapper.bookingToBookingDto(booking1));
            Assertions.assertEquals(actualItem.getNextBooking(), itemMapper.bookingToBookingDto(booking2));
        }

        @Test
        void getItemByIdWhenItemNotFound() {
            NotFoundException exception =
                    assertThrows(NotFoundException.class, () -> itemService.getItemById(user.getId(), item.getId()));
            Assertions.assertEquals(exception.getMessage(), Constants.ITEM_NOT_FOUND);
        }

        @Test
        void getItemByIdWhenCommentEqualNull() {
            when(commentRepository.findAllByItemIdOrderByCreatedDesc(anyLong())).thenReturn(null);
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            ItemDtoResponse actualItem = itemService.getItemById(user.getId(), item.getId());

            assertNotNull(actualItem);
            Assertions.assertEquals(actualItem.getId(), item.getId());
            assertNull(actualItem.getComments());
        }

        @Test
        void getAllByOwner() {
            when(itemRepository.findAllByOwnerIdOrderById(anyLong(), any(Pageable.class))).thenReturn(List.of(item));
            List<ItemDtoResponse> items = itemService.getAll(user.getId(), 0, 1);

            assertFalse(items.isEmpty());
            Assertions.assertEquals(items.get(0).getId(), item.getId());
            Assertions.assertEquals(items.get(0).getName(), item.getName());
            Assertions.assertEquals(items.get(0).getDescription(), item.getDescription());
        }

        @Test
        void searchItemsNormalCase() {
            when(itemRepository.findByNameIgnoreCaseContainingAndAvailableTrueOrDescriptionIgnoreCaseContainingAndAvailableTrue(
                    anyString(), anyString(), any(Pageable.class))).thenReturn(List.of(item));
            List<ItemDto> items = itemService.searchItems(user.getId(), "ружка", 0, 1);

            assertFalse(items.isEmpty());
            Assertions.assertEquals(items.get(0).getId(), item.getId());
            Assertions.assertEquals(items.get(0).getName(), item.getName());
            Assertions.assertEquals(items.get(0).getDescription(), item.getDescription());
        }

        @Test
        void searchItemsIWhenBlancText() {
            List<ItemDto> items = itemService.searchItems(user.getId(), " ", 0, 1);

            assertTrue(items.isEmpty());
        }
    }

    @Nested
    @DisplayName("---ADD COMMENT TESTS---")
    class AddCommentTest {

        @Test
        void addCommentWhenBookingsNotFound() {
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(bookingRepository.findAllByItemIdAndBookerIdAndEndIsBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());
            CommentDto commentDto = new CommentDto().setText("Отличная кружка");

            BadRequestException exception =
                    assertThrows(BadRequestException.class, () -> itemService.addComment(1L, 1L, commentDto));
            Assertions.assertEquals(exception.getMessage(), Constants.NO_COMMENT);
        }

        @Test
        void addCommentWhenUserNotFound() {
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            CommentDto commentDto = new CommentDto().setText("Отличная кружка");

            NotFoundException exception =
                    assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 1L, commentDto));
            Assertions.assertEquals(exception.getMessage(), Constants.USER_NOT_FOUND);
        }

        @Test
        void addCommentWhenItemNotFound() {
            CommentDto commentDto = new CommentDto().setText("Отличная кружка");

            NotFoundException exception =
                    assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 1L, commentDto));
            Assertions.assertEquals(exception.getMessage(), Constants.ITEM_NOT_FOUND);
        }

        @Test
        void addCommentWhenNormalCase() {
            Comment comment = new Comment()
                    .setId(1L)
                    .setAuthor(user)
                    .setItem(item)
                    .setCreated(LocalDateTime.now())
                    .setText(commentDto.getText());
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(bookingRepository.findAllByItemIdAndBookerIdAndEndIsBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                    .thenReturn(List.of(booking1));
            when(commentRepository.save(any(Comment.class))).thenReturn(comment);

            CommentDtoResponse commentDtoResponse = itemService.addComment(user.getId(), item.getId(), commentDto);

            assertNotNull(commentDtoResponse);
            Assertions.assertEquals(commentDtoResponse.getId(), comment.getId());
            Assertions.assertEquals(commentDtoResponse.getCreated(), comment.getCreated());
            Assertions.assertEquals(commentDtoResponse.getText(), comment.getText());
            Assertions.assertEquals(commentDtoResponse.getAuthorName(), comment.getAuthor().getName());
        }
    }
}