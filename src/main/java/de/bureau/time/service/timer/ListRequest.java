package de.bureau.time.service.timer;

import de.bureau.time.utils.DateTimeUtils;

import java.time.LocalDate;

import static org.apache.commons.lang3.Validate.notNull;

public class ListRequest {
    private final LocalDate date;

    public ListRequest(LocalDate date) {
        this.date = notNull(date);
    }

    public LocalDate getDate() {
        return date;
    }

    public static ListRequestBuilder builder() {
        return new ListRequestBuilder();
    }

    public static ListRequest forToday() {
        return new ListRequest(DateTimeUtils.todayInUtc());
    }

    public static final class ListRequestBuilder {
        private LocalDate date;

        private ListRequestBuilder() {
        }

        public ListRequestBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public ListRequest build() {
            return new ListRequest(date);
        }
    }
}
