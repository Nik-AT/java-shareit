package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByOwnerId(Long ownerId, Pageable pageRequest);

    List<Item> findByNameContainsOrDescriptionContainsIgnoreCase(String text, String text1, Pageable pageRequest);
}
