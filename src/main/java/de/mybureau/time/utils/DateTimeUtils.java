package de.mybureau.time.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;

public final class DateTimeUtils {

    private DateTimeUtils() {

    }

    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    public static LocalDateTime endOfDay(LocalDate date) {
        // why not LocalTime.MAX see org.postgresql.jdbc.MAX_NANOS_BEFORE_WRAP_ON_ROUND
        return LocalDateTime.of(date, LocalTime.of(23, 59, 59, 999_999_000));
    }

    public static LocalDateTime nowInUtc() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public static LocalDate todayInUtc() {
        return LocalDate.now(ZoneOffset.UTC);
    }

    public static Date nowInUtcAsLegacyDate() {
        return Date.from(nowInUtc().toInstant(ZoneOffset.UTC));
    }


    public static Date fromUtcLocalDateTime(LocalDateTime dateTime) {
        return Date.from(dateTime.toInstant(ZoneOffset.UTC));
    }
}
