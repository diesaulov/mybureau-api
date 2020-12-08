package de.mybureau.time.api;

import de.mybureau.time.api.dto.NewProjectTaskDto;
import de.mybureau.time.api.dto.ProjectDto;
import de.mybureau.time.api.dto.ProjectTaskDto;
import de.mybureau.time.service.project.NewProjectTaskRequest;
import de.mybureau.time.service.project.ProjectService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public List<ProjectTaskDto> tasks(@PathVariable("projectId") long projectId) {
        return projectService.taskList(projectId).stream()
                .map(ProjectTaskDto::from)
                .collect(Collectors.toList());
    }

    @PostMapping("/{projectId}/tasks")
    public ProjectTaskDto newTask(@PathVariable("projectId") long projectId,
                                  @RequestBody @Valid NewProjectTaskDto newProjectTaskDto) {
        final var newTask = projectService.addTask(NewProjectTaskRequest.builder()
                .name(newProjectTaskDto.name)
                .description(newProjectTaskDto.description)
                .projectId(projectId)
                .build());
        return ProjectTaskDto.from(newTask);
    }
}
