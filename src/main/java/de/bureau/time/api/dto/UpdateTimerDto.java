package de.bureau.time.api.dto;

import javax.validation.constraints.NotBlank;

public class UpdateTimerDto {
    @NotBlank public String notes;
}
