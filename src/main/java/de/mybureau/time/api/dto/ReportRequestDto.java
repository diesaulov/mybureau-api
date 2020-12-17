package de.mybureau.time.api.dto;

import de.mybureau.time.service.timer.PeriodGroupBy;
import de.mybureau.time.service.timer.TaskGroupBy;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class ReportRequestDto {
    public LocalDate from;
    public LocalDate to;
    @NotNull
    public PeriodGroupBy periodGroupBy;
    @NotNull
    public TaskGroupBy taskGroupBy;
}
