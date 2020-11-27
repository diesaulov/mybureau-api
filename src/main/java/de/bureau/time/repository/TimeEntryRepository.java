package de.bureau.time.repository;

import de.bureau.time.model.TimerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimerEntry, Long> {

    Stream<TimerEntry> findByDeletedFalseAndTimerStartedIsBetween(LocalDateTime from, LocalDateTime to);

    Stream<TimerEntry> findByDeletedFalseAndDate(LocalDate localDate);

    Optional<TimerEntry> findByIdAndDeletedFalse(long id);
}
