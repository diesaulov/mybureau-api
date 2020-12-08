package de.bureau.time.api.dto;

import de.bureau.time.service.timer.TimerGroupBy;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class ReportRequestDto {
    public LocalDate date;
    @NotNull public TimerGroupBy groupBy;
}
