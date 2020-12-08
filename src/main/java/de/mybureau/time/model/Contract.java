package de.mybureau.time.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Currency;

@Entity
@Table(name = "contract")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "rate_in_cents_per_hour")
    private long rateInCentsPerHour;

    @Column(name = "rate_currency")
    private String rateCurrency;

    public long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public long getRateInCentsPerHour() {
        return rateInCentsPerHour;
    }

    public void setRateInCentsPerHour(long rateInCentsPerHour) {
        this.rateInCentsPerHour = rateInCentsPerHour;
    }

    public String getRateCurrency() {
        return rateCurrency;
    }

    public void setRateCurrency(String rateCurrency) {
        this.rateCurrency = rateCurrency;
    }

    public Money ratePerHour() {
        return Money.of(rateInCentsPerHour, Currency.getInstance(rateCurrency));
    }
}
