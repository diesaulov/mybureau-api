package de.mybureau.time.service.timer;

import de.mybureau.time.model.TimerEntry;
import de.mybureau.time.repository.TimerEntryRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final TimerEntryRepository timerEntryRepository;

    public ReportService(TimerEntryRepository timerEntryRepository) {
        this.timerEntryRepository = timerEntryRepository;
    }

    @Transactional
    public List<ReportEntry> report(ReportRequest reportRequest) {

        final var from = reportRequest.getFrom();
        final var to = reportRequest.getTo();
        final var result = new ArrayList<ReportEntry>();
        final var projectId = reportRequest.getProjectId();

        if (reportRequest.getPeriodGroupBy() == PeriodGroupBy.DAY) {
            for (LocalDate date = from; date.isBefore(to); date = date.plusDays(1)) {
                final var entries = entries(date, date.plusDays(1), projectId);
                result.addAll(groupByTasks(entries, date.toString(), reportRequest.getTaskGroupBy()));
            }
        } else if (reportRequest.getPeriodGroupBy() == PeriodGroupBy.WEEK) {
            for (LocalDate weekStart = from.with(DayOfWeek.MONDAY); weekStart.isBefore(to); weekStart = weekStart.plusDays(7)) {
                final var entries = entries(weekStart, weekStart.plusDays(7), projectId);
                final var weekNum = weekStart.get(WeekFields.ISO.weekOfYear());
                final var weekLabel = weekStart.getYear() + "W" + weekNum;
                result.addAll(groupByTasks(entries, weekLabel, reportRequest.getTaskGroupBy()));
            }
        } else if(reportRequest.getPeriodGroupBy() == PeriodGroupBy.MONTH) {
            for (LocalDate monthStart = from.withDayOfMonth(1); monthStart.isBefore(to); monthStart = monthStart.plusMonths(1)) {
                final var entries = entries(monthStart, monthStart.plusMonths(1), projectId);
                final var monthLabel = monthStart.getYear() + "-" + monthStart.getMonthValue();
                result.addAll(groupByTasks(entries, monthLabel, reportRequest.getTaskGroupBy()));
            }
        }

        return result.stream()
                .sorted(Comparator.comparing(ReportEntry::getPeriodLabel).reversed())
                .collect(Collectors.toUnmodifiableList());
    }

    private List<TimerEntry> entries(LocalDate fromInclusive, LocalDate toExclusive, Long projectId) {
        if (projectId == null) {
            return timerEntryRepository.findByDeletedFalseAndStartedIsBetween(fromInclusive.atStartOfDay(), toExclusive.atStartOfDay());
        } else {
            return timerEntryRepository.findByDeletedFalseAndStartedIsBetween(fromInclusive.atStartOfDay(), toExclusive.atStartOfDay())
                    .stream()
                    .filter(t -> t.getTask().getProject().getId() == projectId)
                    .collect(Collectors.toUnmodifiableList());
        }
    }

    private List<ReportEntry> groupByTasks(List<TimerEntry> entries, String periodLabel, TaskGroupBy taskGroupBy) {
        if (taskGroupBy == TaskGroupBy.NOTES) {
            return group(entries, GroupByNotesKey::from, periodLabel);
        } else if (taskGroupBy == TaskGroupBy.TASK) {
            return group(entries, t -> t.getTask().getId(), periodLabel);
        } else if (taskGroupBy == TaskGroupBy.PROJECT) {
            return group(entries, t -> t.getTask().getProject().getId(), periodLabel);
        } else {
            return List.of();
        }
    }

    private <K> List<ReportEntry> group(List<TimerEntry> entries, Function<TimerEntry, K> keyExtractor, String periodLabel) {
        final var mapByNotes = new HashMap<K, ReportEntry>();
        for (TimerEntry timerEntry : entries) {
            final var key = keyExtractor.apply(timerEntry);
            mapByNotes.computeIfPresent(key, (k, v) -> v.accumulate(timerEntry.getDurationInSeconds()));
            mapByNotes.computeIfAbsent(key, k -> ReportEntry.init(periodLabel, timerEntry));
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