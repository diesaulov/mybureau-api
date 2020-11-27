package de.bureau.time.api;

import de.bureau.time.api.dto.ProjectDto;
import de.bureau.time.api.dto.ProjectTaskTypeDto;
import de.bureau.time.service.project.ProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/projects")
public class ProjectApi {

    private final ProjectService projectService;

    public ProjectApi(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectDto> list() {
        return projectService.list().stream()
                .map(ProjectDto::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/{projectId}/tasks")
    public List<ProjectTaskTypeDto> tasks(@PathVariable("projectId") long projectId) {
        return projectService.taskTypes(projectId).stream()
                .map(ProjectTaskTypeDto::from)
                .collect(Collectors.toList());
    }
}
