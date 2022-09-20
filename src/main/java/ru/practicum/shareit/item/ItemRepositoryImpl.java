package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, List<Item>> items = new HashMap<>();

    private long lastId = 1;

    @Override
    public Item save(Long userId, Item item) {
        getId(item);
        List<Item> itemList = items.get(userId);
        if (itemList == null) {
            itemList = new ArrayList<>();
            itemList.add(item);
            items.put(userId, itemList);
            return item;
        }
        itemList.removeIf(element -> element.getId().equals(item.getId()));
        itemList.add(item);
        items.put(userId, itemList);
        return item;
    }

    @Override
    public Item get(Long userId, Long itemId) {
        List<Item> itemList = items.get(userId);
        return itemList == null ? null : items.get(userId).stream()
                .filter(element -> element.getId().equals(itemId))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public Item find(Long itemId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public List<Item> get(Long userId) {
        return items.get(userId);
    }

    @Override
    public void delete(Long userId, Long itemId) {
        List<Item> itemList = items.get(userId);
        if (itemList != null) {
            itemList.removeIf(element -> element.getId().equals(itemId));
        }
    }

    @Override
    public List<Item> get() {
        return items.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getAvailable().equals(true))
                .filter(item -> (item.getName().toUpperCase(Locale.ROOT).contains(text.toUpperCase(Locale.ROOT)) ||
                        item.getDescription().toUpperCase(Locale.ROOT).contains(text.toUpperCase(Locale.ROOT))))
                .collect(Collectors.toList());
    }

    private void getId(Item item) {
        if (item.getId() == null) {
            item.setId(lastId);
            lastId++;
        }
    }
}
