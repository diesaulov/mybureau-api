package de.mybureau.time.service.timer;

import java.time.LocalDate;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public class ReportRequest {
    private final LocalDate from;
    private final LocalDate to;
    private final PeriodGroupBy periodGroupBy;
    private final TaskGroupBy taskGroupBy;

    public ReportRequest(LocalDate from,
                         LocalDate to,
                         PeriodGroupBy periodGroupBy,
                         TaskGroupBy taskGroupBy) {
        isTrue(to.isAfter(from));
        this.from = notNull(from);
        this.to = notNull(to);
        this.periodGroupBy = notNull(periodGroupBy);
        this.taskGroupBy = notNull(taskGroupBy);
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public PeriodGroupBy getPeriodGroupBy() {
        return periodGroupBy;
    }

    public TaskGroupBy getTaskGroupBy() {
        return taskGroupBy;
    }

    public static ListRequestBuilder builder() {
        return new ListRequestBuilder();
    }

    public static final class ListRequestBuilder {
        private LocalDate from;
        private LocalDate to;
        private PeriodGroupBy periodGroupBy;
        private TaskGroupBy taskGroupBy;

        private ListRequestBuilder() {
        }

        public ListRequestBuilder from(LocalDate from) {
            this.from = from;
            return this;
        }

        public ListRequestBuilder to(LocalDate to) {
            this.to = to;
            return this;
        }

        public ListRequestBuilder taskGroupBy(TaskGroupBy taskGroupBy) {
            this.taskGroupBy = taskGroupBy;
            return this;
        }

        public ListRequestBuilder periodGroupBy(PeriodGroupBy periodGroupBy) {
            this.periodGroupBy = periodGroupBy;
            return this;
        }

        public ReportRequest build() {
            return new ReportRequest(from, to, periodGroupBy, taskGroupBy);
        }
    }
}
