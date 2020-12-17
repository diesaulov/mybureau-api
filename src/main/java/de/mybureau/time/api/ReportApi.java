package de.mybureau.time.api;

import de.mybureau.time.api.dto.ReportDto;
import de.mybureau.time.api.dto.ReportRequestDto;
import de.mybureau.time.service.timer.PeriodGroupBy;
import de.mybureau.time.service.timer.ReportRequest;
import de.mybureau.time.service.timer.ReportService;
import de.mybureau.time.service.timer.TaskGroupBy;
import de.mybureau.time.utils.DateTimeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/reports")
public class ReportApi {

    private final ReportService reportService;

    public ReportApi(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public List<ReportDto> report(@RequestBody @Valid ReportRequestDto reportRequestDto) {
        final var from = reportRequestDto.from == null ? DateTimeUtils.todayInUtc() : reportRequestDto.from;
        final var to = reportRequestDto.to == null ? from.plusDays(1) : reportRequestDto.to;
        final var reportReq = ReportRequest
                .builder()
                .from(from).to(to)
                .taskGroupBy(reportRequestDto.taskGroupBy == null ? TaskGroupBy.TASK : reportRequestDto.taskGroupBy)
                .periodGroupBy(reportRequestDto.periodGroupBy == null ? PeriodGroupBy.DAY : reportRequestDto.periodGroupBy)
                .build();
        return reportService.report(reportReq)
                .stream()
                .map(ReportDto::from)
                .collect(Collectors.toUnmodifiableList());
    }
}