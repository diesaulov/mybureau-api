package de.mybureau.time.api.dto;

import de.mybureau.time.model.Project;

public class ProjectDto {
    public String id;
    public String name;
    public String code;
    public ClientDto client;

    public static ProjectDto from(Project project) {
        final var dto = new ProjectDto();
        dto.id = Long.toString(project.getId());
        dto.name = project.getName();
        dto.client = ClientDto.from(project.getClient());
        return dto;
    }
}
