package de.bureau.time.api.dto;

import de.bureau.time.model.ProjectTask;

public class ProjectTaskTypeDto {
    public String id;
    public String code;
    public String name;
    public String projectName;

    public static ProjectTaskTypeDto from(ProjectTask projectTask) {
        final var dto = new ProjectTaskTypeDto();
        dto.id = Long.toString(projectTask.getId());
        dto.name = projectTask.getName();
        dto.projectName = projectTask.getProject().getName();
        return dto;
    }
}
