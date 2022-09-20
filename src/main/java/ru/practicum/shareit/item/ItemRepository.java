package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item save(Long userId, Item item);

    Item get(Long userId, Long itemId);

    Item find(Long itemId);

    List<Item> get(Long userId);

    void delete(Long userId, Long itemId);

    List<Item> get();

    List<Item> search(String text);
}
