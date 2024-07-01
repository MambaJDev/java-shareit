package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findALLByItem(Item item);

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime date);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime date);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long ownerId, LocalDateTime before, LocalDateTime after);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long bookerId, LocalDateTime before, LocalDateTime after);

    List<Booking> findALLByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime date);

    List<Booking> findALLByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime date);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    List<Booking> findAllByItemIdAndBookerIdAndEndIsBefore(Long itemId, Long bookerId, LocalDateTime localDateTime);
}