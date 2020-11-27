package de.bureau.time.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public final class DateTimeUtils {

    private DateTimeUtils() {

    }

    public static LocalDateTime nowInUtc() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public static LocalDate todayInUtc() {
        return LocalDate.now(ZoneOffset.UTC);
    }

    public static Date nowDateInUtc() {
        return Date.from(nowInUtc().toInstant(ZoneOffset.UTC));
    }

    public static Date fromUtcLocalDateTime(LocalDateTime dateTime) {
        return Date.from(dateTime.toInstant(ZoneOffset.UTC));
    }
}
