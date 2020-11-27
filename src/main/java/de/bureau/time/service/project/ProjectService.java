package de.bureau.time.service.project;

import de.bureau.time.model.Project;
import de.bureau.time.model.ProjectTask;
import de.bureau.time.repository.ProjectRepository;
import de.bureau.time.repository.ProjectTaskRepository;
import de.bureau.time.service.project.exception.ProjectNotFoundException;
import de.bureau.time.service.project.exception.ProjectTaskTypeNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectTaskRepository projectTaskRepository;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectTaskRepository projectTaskRepository) {
        this.projectRepository = projectRepository;
        this.projectTaskRepository = projectTaskRepository;
    }

    @Transactional
    public List<Project> list() {
        return projectRepository.findAll();
    }

    @Transactional
    public List<ProjectTask> taskTypes(long projectId) {
        final var project = project(projectId);
        return projectTaskRepository.findByProject(project);
    }

    @Transactional
    public ProjectTask taskType(long id) {
        return projectTaskRepository.findById(id)
                .orElseThrow(() -> ProjectTaskTypeNotFoundException.forId(id));
    }

    @Transactional
    public Project project(long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> ProjectNotFoundException.forId(projectId));
    }
}
