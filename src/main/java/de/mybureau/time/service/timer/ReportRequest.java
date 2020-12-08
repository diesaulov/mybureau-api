package de.mybureau.time.service.timer;

import de.mybureau.time.utils.DateTimeUtils;

import java.time.LocalDate;

import static org.apache.commons.lang3.Validate.notNull;

public class ReportRequest {
    private final LocalDate date;
    private final TimerGroupBy groupBy;

    public ReportRequest(LocalDate date, TimerGroupBy groupBy) {
        this.date = notNull(date);
        this.groupBy = notNull(groupBy);
    }

    public LocalDate getDate() {
        return date;
    }

    public TimerGroupBy getGroupBy() {
        return groupBy;
    }

    public static ListRequestBuilder builder() {
        return new ListRequestBuilder();
    }

    public static ReportRequest forToday() {
        return new ReportRequest(DateTimeUtils.todayInUtc(), TimerGroupBy.NONE);
    }

    public static final class ListRequestBuilder {
        private LocalDate date;
        private TimerGroupBy groupBy;

        private ListRequestBuilder() {
        }

        public ListRequestBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public ListRequestBuilder groupBy(TimerGroupBy groupBy) {
            this.groupBy = groupBy;
            return this;
        }

        public ReportRequest build() {
            return new ReportRequest(date, groupBy);
        }
    }
}
