package de.mybureau.time.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class DateTimeHelper {

    public LocalDateTime nowInUtc() {
        return DateTimeUtils.nowInUtc();
    }

    public LocalDate todayInUtc() {
        return DateTimeUtils.todayInUtc();
    }

    public Date nowInUtcAsLegacyDate() {
        return DateTimeUtils.nowInUtcAsLegacyDate();
    }

}
