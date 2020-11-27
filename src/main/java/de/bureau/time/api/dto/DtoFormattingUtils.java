package de.bureau.time.api.dto;

import de.bureau.time.model.Money;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DtoFormattingUtils {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String formatLocalDate(LocalDate localDate) {
        return localDate == null ? null : formatter.format(localDate);
    }

    public static String formatMoneyWithCurrency(Money money) {
        if (money != null) {
            return money.formattedValue() + " " + money.getCurrency().getDisplayName();
        } else {
            return null;
        }
    }
}
