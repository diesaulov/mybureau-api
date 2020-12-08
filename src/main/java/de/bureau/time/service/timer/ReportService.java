package de.bureau.time.service.timer;

import de.bureau.time.model.TimerEntry;
import de.bureau.time.repository.TimeEntryRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportService {

    private final TimeEntryRepository timeEntryRepository;

    public ReportService(TimeEntryRepository timeEntryRepository) {
        this.timeEntryRepository = timeEntryRepository;
    }

    @Transactional
    public List<ReportEntry> report(ReportRequest reportRequest) {
        final var date = reportRequest.getDate();
        final var entries = Stream.concat(
                timeEntryRepository.findByDeletedFalseAndTimerStartedIsBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay()),
                timeEntryRepository.findByDeletedFalseAndDate(date))
                .collect(Collectors.toUnmodifiableList());

        if (reportRequest.getGroupBy() == TimerGroupBy.NOTES) {
            return group(entries, GroupByNotesKey::from);
        } else if (reportRequest.getGroupBy() == TimerGroupBy.TASK) {
            return group(entries, t -> t.getTask().getId());
        } else if (reportRequest.getGroupBy() == TimerGroupBy.PROJECT) {
            return group(entries, t -> t.getTask().getProject().getId());
        } else {
            return List.of();
        }
    }

    private <K> List<ReportEntry> group(List<TimerEntry> entries, Function<TimerEntry, K> keyExtractor) {
        final var mapByNotes = new HashMap<K, ReportEntry>();
        for (TimerEntry timerEntry : entries) {
            final var key = keyExtractor.apply(timerEntry);
            mapByNotes.computeIfPresent(key, (k, v) -> v.accumulate(timerEntry.calculateDurationInMinutes()));
            mapByNotes.computeIfAbsent(key, k -> ReportEntry.init(timerEntry));
        }
        return List.copyOf(mapByNotes.values());
    }
}

class GroupByNotesKey {
    private final long taskId;
    private final String notes;

    GroupByNotesKey(long taskId, String notes) {
        this.taskId = taskId;
        this.notes = notes;
    }

    static GroupByNotesKey from(TimerEntry timerEntry) {
        return new GroupByNotesKey(timerEntry.getTask().getId(), timerEntry.getNotes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupByNotesKey that = (GroupByNotesKey) o;
        return taskId == that.taskId &&
                Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, notes);
    }
}