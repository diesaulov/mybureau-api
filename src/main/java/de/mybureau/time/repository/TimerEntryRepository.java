package de.mybureau.time.repository;

import de.mybureau.time.model.TimerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimerEntryRepository extends JpaRepository<TimerEntry, Long> {

    List<TimerEntry> findByDeletedFalseAndStartedIsBetween(LocalDateTime from, LocalDateTime to);

    @Query("select te from TimerEntry te " +
            "where te.deleted = false and te.type = 'TIMER' and te.durationInSeconds is null and te.started between ?1 and ?2")
    List<TimerEntry> findRunningTimers(LocalDateTime from, LocalDateTime to);

    @Query("select count(te) from TimerEntry te " +
            "where te.deleted = false and te.type = 'TIMER' and te.durationInSeconds is null and te.started between ?1 and ?2")
    int countRunningTimers(LocalDateTime from, LocalDateTime to);

    @Query("select te from TimerEntry te " +
            "where te.deleted = false and te.type = 'TIMER' and te.durationInSeconds is not null and te.started between ?1 and ?2")
    List<TimerEntry> findStoppedTimers(LocalDateTime from, LocalDateTime to);

    Optional<TimerEntry> findByIdAndDeletedFalse(long id);
}
