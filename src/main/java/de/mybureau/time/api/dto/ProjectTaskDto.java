package de.mybureau.time.api.dto;

import de.mybureau.time.model.ProjectTask;

public class ProjectTaskDto {
    public String id;
    public String code;
    public String name;
    public String description;
    public String projectName;

    public static ProjectTaskDto from(ProjectTask projectTask) {
        final var dto = new ProjectTaskDto();
        dto.id = Long.toString(projectTask.getId());
        dto.name = projectTask.getName();
        dto.projectName = projectTask.getProject().getName();
        dto.description = projectTask.getDescription();
        return dto;
    }
}
