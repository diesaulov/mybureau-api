package de.mybureau.time.model;

import java.util.Currency;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public class Money {
    private final long cents;
    private final Currency currency;

    private Money(long cents, Currency currency) {
        this.cents = cents;
        this.currency = currency;
    }

    public long getCents() {
        return cents;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String formattedValue() {
        return String.format("%#." + currency.getDefaultFractionDigits() + "f",
                (double) cents / Math.pow(10, currency.getDefaultFractionDigits()));
    }

    public static Money of(long cents, Currency currency) {
        isTrue(cents >= 0, "Cents value cannot be negative!");
        return new Money(cents, notNull(currency, "Currency cannot be null!"));
    }

    public static Money inEur(long cents) {
        return new Money(cents, Currency.getInstance("EUR"));
    }
}
