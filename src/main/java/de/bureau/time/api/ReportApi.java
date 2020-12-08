package de.bureau.time.api;

import de.bureau.time.api.dto.ReportDto;
import de.bureau.time.api.dto.ReportRequestDto;
import de.bureau.time.service.timer.ReportRequest;
import de.bureau.time.service.timer.ReportService;
import de.bureau.time.utils.DateTimeUtils;
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
        final var reportReq = ReportRequest
                .builder()
                .date(reportRequestDto.date == null ? DateTimeUtils.todayInUtc() : reportRequestDto.date)
                .groupBy(reportRequestDto.groupBy)
                .build();
        return reportService.report(reportReq)
                .stream()
                .map(ReportDto::from)
                .collect(Collectors.toUnmodifiableList());
    }
}