package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderById(Long ownerId);

    List<Item> findByNameIgnoreCaseContainingAndAvailableTrueOrDescriptionIgnoreCaseContainingAndAvailableTrue(String name, String description);
}