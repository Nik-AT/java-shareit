package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByUserIdNot(Long userId, Pageable pageRequest);

    @Modifying
    @Query("select ir from ItemRequest as ir " +
            "where ir.userId = ?1 " +
            "order by ir.creationTime desc ")
    List<ItemRequest> findAllByUserId(Long userId);
}
