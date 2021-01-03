package de.mybureau.time.api;

import de.mybureau.time.api.dto.CsvReportDto;
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
import java.time.format.DateTimeFormatter;
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
        return reportService.report(prepareReportRequest(reportRequestDto))
                .stream()
                .map(ReportDto::from)
                .collect(Collectors.toUnmodifiableList());
    }

    private ReportRequest prepareReportRequest(ReportRequestDto reportRequestDto) {
        final var from = reportRequestDto.from == null ? DateTimeUtils.todayInUtc() : reportRequestDto.from;
        final var to = reportRequestDto.to == null ? from.plusDays(1) : reportRequestDto.to;
        return ReportRequest
                .builder()
                .from(from).to(to)
                .taskGroupBy(reportRequestDto.taskGroupBy == null ? TaskGroupBy.TASK : reportRequestDto.taskGroupBy)
                .periodGroupBy(reportRequestDto.periodGroupBy == null ? PeriodGroupBy.DAY : reportRequestDto.periodGroupBy)
                .projectId(reportRequestDto.projectId)
                .build();
    }

    @PostMapping("/csv")
    public CsvReportDto asCsv(@RequestBody @Valid ReportRequestDto reportRequestDto) {

        final var reportReq = prepareReportRequest(reportRequestDto);
        final var reportEntries = reportService.report(reportReq);
        final var csvContentBuilder = new StringBuilder();

        final var delimiter = ",";
        final var textQuote = "\"";

        if (reportRequestDto.taskGroupBy == TaskGroupBy.TASK) {
            csvContentBuilder.append("Period").append(delimiter)
                    .append("Project").append(delimiter)
                    .append("Task").append(delimiter)
                    .append("Duration").append(delimiter)
                    .append("\n");

            reportEntries.forEach(r -> csvContentBuilder
                    .append(r.getPeriodLabel()).append(delimiter)
                    .append(reportStringValue(r.getProject().getName())).append(delimiter)
                    .append(reportStringValue(r.getTask().getName())).append(delimiter)
                    .append(r.getDurationInSeconds() / 3600.0).append(delimiter)
                    .append("\n"));
        } else if(reportRequestDto.taskGroupBy == TaskGroupBy.NOTES) {
            csvContentBuilder.append("Period").append(delimiter)
                    .append("Project").append(delimiter)
                    .append("Task").append(delimiter)
                    .append("Notes").append(delimiter)
                    .append("Duration").append(delimiter)
                    .append("\n");

            reportEntries.forEach(r -> csvContentBuilder
                    .append(r.getPeriodLabel()).append(delimiter)
                    .append(reportStringValue(r.getProject().getName())).append(delimiter)
                    .append(reportStringValue(r.getTask().getName())).append(delimiter)
                    .append(reportStringValue(r.getNotes())).append(delimiter)
                    .append(r.getDurationInSeconds() / 3600.0).append(delimiter)
                    .append("\n"));
        } else if (reportRequestDto.taskGroupBy == TaskGroupBy.PROJECT) {
            csvContentBuilder.append("Period").append(delimiter)
                    .append("Project").append(delimiter)
                    .append("Duration").append(delimiter)
                    .append("\n");

            reportEntries.forEach(r -> csvContentBuilder
                    .append(r.getPeriodLabel()).append(delimiter)
                    .append(reportStringValue(r.getProject().getName())).append(delimiter)
                    .append(r.getDurationInSeconds() / 3600.0).append(delimiter)
                    .append("\n"));
        }

        final var csvReportDto = new CsvReportDto();
        csvReportDto.from = reportReq.getFrom().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        csvReportDto.to = reportReq.getTo().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        csvReportDto.content = csvContentBuilder.toString();
        return csvReportDto;
    }

    private String reportStringValue(String value) {
        return String.format("\"%s\"", value.replace(",", ";"));
    }
}