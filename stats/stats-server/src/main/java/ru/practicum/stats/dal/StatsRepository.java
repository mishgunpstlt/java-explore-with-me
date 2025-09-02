package ru.practicum.stats.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.model.EndpointHit;

import java.time.Instant;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select eh.app, eh.uri, count(distinct eh.ip) as views from EndpointHit eh " +
            "where eh.timestamp between ?1 and ?2 " +
            "and (?3 is null or eh.uri in ?3) " +
            "group by eh.app, eh.uri " +
            "order by views desc")
    List<Object[]> findUniqueStats(Instant start, Instant end, List<String> uris);

    @Query("select eh.app, eh.uri, count(eh) as views from EndpointHit eh " +
            "where eh.timestamp between ?1 and ?2 " +
            "and (?3 is null or eh.uri in ?3) " +
            "group by eh.app, eh.uri " +
            "order by views desc")
    List<Object[]> findAllStats(Instant start, Instant end, List<String> uris);
}
