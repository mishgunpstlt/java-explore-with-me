package ru.practicum.repository.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {


    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> ids);

    Integer countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    Boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findAllByRequesterId(Long userId);

    @Query("SELECT r.event.id, COUNT(r) FROM Request r " +
            "WHERE r.status = 'CONFIRMED' AND r.event.id IN :eventIds " +
            "GROUP BY r.event.id")
    List<Object[]> countConfirmedRequestsByEventIds(@Param("eventIds") List<Long> eventIds);


}
