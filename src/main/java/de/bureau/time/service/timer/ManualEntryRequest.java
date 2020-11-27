package de.bureau.time.service.timer;

import java.time.LocalDate;

import static de.bureau.time.utils.DateTimeUtils.todayInUtc;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;

public class ManualEntryRequest {
    private final int taskTypeId;
    private final int durationInMinutes;
    private final LocalDate date;
    private final String notes;

    private ManualEntryRequest(int taskTypeId, int durationInMinutes, LocalDate date, String notes) {
        isTrue(taskTypeId > 0);
        isTrue(durationInMinutes > 0, "Duration has to be > 0");
        isTrue(durationInMinutes <= 480, "Maximum 480 minutes of duration allowed!");
        if (date != null && date.isAfter(todayInUtc())) {
            throw new IllegalArgumentException("The date has to be today or in the past!");
        }
        this.taskTypeId = taskTypeId;
        this.durationInMinutes = durationInMinutes;
        this.date = date == null ? todayInUtc() : date;
        this.notes = notBlank(notes, "Notes cannot be blank!");
    }

    public int getTaskTypeId() {
        return taskTypeId;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getNotes() {
        return notes;
    }

    public static ManualTimerRequestBuilder builder() {
        return new ManualTimerRequestBuilder();
    }


    public static final class ManualTimerRequestBuilder {
        private int taskTypeId;
        private int durationInMinutes;
        private LocalDate date;
        private String notes;

        private ManualTimerRequestBuilder() {
        }

        public ManualTimerRequestBuilder taskTypeId(int taskTypeId) {
            this.taskTypeId = taskTypeId;
            return this;
        }

        public ManualTimerRequestBuilder durationInMinutes(int durationInMinutes) {
            this.durationInMinutes = durationInMinutes;
            return this;
        }

        public ManualTimerRequestBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public ManualTimerRequestBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public ManualEntryRequest build() {
            return new ManualEntryRequest(taskTypeId, durationInMinutes, date, notes);
        }
    }
}
