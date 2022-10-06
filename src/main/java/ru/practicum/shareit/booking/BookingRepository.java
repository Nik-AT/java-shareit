package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "select * from BOOKING where BOOKER_ID = ?1", nativeQuery = true)
    List<Booking> findAllByBookerId(Long bookerId);

    @Query(value = "select * from BOOKING where BOOKER_ID = ?1 and STATUS = ?2", nativeQuery = true)
    List<Booking> findAllByBookerIdAndStatus(Long bookerId, String status);

    @Query(value = "select b.ID, b.START_DATE, b.END_DATE, b.ITEM_ID, b.BOOKER_ID,b.STATUS from BOOKING as b " +
            "JOIN ITEMS I on b.ITEM_ID = I.ID where OWNER_ID = ?1",
            nativeQuery = true)
    List<Booking> findAllByOwner(Long userId);

    @Query(value = "select * from BOOKING where ITEM_ID = ?1 order by START_DATE", nativeQuery = true)
    List<Booking> findAllByItemId(Long itemId);
}
