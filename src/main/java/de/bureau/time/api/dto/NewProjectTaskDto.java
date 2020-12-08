package de.bureau.time.api.dto;

import javax.validation.constraints.NotBlank;

public class NewProjectTaskDto {
    @NotBlank public String name;
    @NotBlank public String description;
}
