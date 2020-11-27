package de.bureau.time.api.dto;

import de.bureau.time.model.Contract;

import static de.bureau.time.api.dto.DtoFormattingUtils.formatLocalDate;
import static de.bureau.time.api.dto.DtoFormattingUtils.formatMoneyWithCurrency;

public class ContractDto {
    public long id;
    public String startDate;
    public String endDate;
    public String project;
    public String client;
    public String ratePerHour;

    public static ContractDto from(Contract contract) {
        final var dto = new ContractDto();
        dto.id = contract.getId();
        dto.startDate = formatLocalDate(contract.getStartDate());
        dto.endDate = formatLocalDate(contract.getEndDate());
        dto.project = contract.getProject().getName();
        dto.client = contract.getProject().getClient().getName();
        dto.ratePerHour = formatMoneyWithCurrency(contract.ratePerHour());
        return dto;
    }
}
