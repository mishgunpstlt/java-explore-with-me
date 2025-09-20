package ru.practicum.repository.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Boolean existsByCategoryId(Long categoryId);

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%')) " +
            "    OR LOWER(e.description) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (COALESCE(:start, e.eventDate) <= e.eventDate) " +
            "AND (COALESCE(:end, e.eventDate) >= e.eventDate) " +
            "AND (:paid IS NULL OR e.paid = :paid)")
    Page<Event> searchEvent(@Param("text") String text,
                            @Param("categories") List<Long> categories,
                            @Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end,
                            @Param("paid") Boolean paid,
                            Pageable pageable);


    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%')) " +
            "    OR LOWER(e.description) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND e.eventDate >= CURRENT_TIMESTAMP " +
            "AND (:paid IS NULL OR e.paid = :paid)")
    Page<Event> searchEventCurrentTime(@Param("text") String text,
                                       @Param("categories") List<Long> categories,
                                       @Param("paid") Boolean paid,
                                       Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (:state IS NULL OR e.state IN :state) " +
            "AND (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (COALESCE(:start, e.eventDate) <= e.eventDate) " +
            "AND (COALESCE(:end, e.eventDate) >= e.eventDate)")
    Page<Event> searchEventAdmin(@Param("users") List<Long> users,
                                 @Param("state") List<EventStatus> state,
                                 @Param("categories") List<Long> categories,
                                 @Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 Pageable pageable);
}
