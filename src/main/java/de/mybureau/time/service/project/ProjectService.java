package de.mybureau.time.service.project;

import de.mybureau.time.model.Project;
import de.mybureau.time.model.ProjectTask;
import de.mybureau.time.repository.ProjectRepository;
import de.mybureau.time.repository.ProjectTaskRepository;
import de.mybureau.time.service.project.exception.ProjectNotFoundException;
import de.mybureau.time.service.project.exception.ProjectTaskTypeNotFoundException;
import de.mybureau.time.utils.DateTimeUtils;
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
    public List<ProjectTask> taskList(long projectId) {
        final var project = project(projectId);
        return projectTaskRepository.findByProject(project);
    }

    @Transactional
    public ProjectTask addTask(NewProjectTaskRequest newProjectTaskRequest) {
        final var newProjectTask = new ProjectTask();
        newProjectTask.setName(newProjectTaskRequest.getName());
        newProjectTask.setDescription(newProjectTaskRequest.getDescription());
        newProjectTask.setProject(project(newProjectTaskRequest.getProjectId()));
        newProjectTask.setCreatedTs(DateTimeUtils.nowInUtc());
        return projectTaskRepository.save(newProjectTask);
    }

    @Transactional
    public ProjectTask task(long id) {
        return projectTaskRepository.findById(id)
                .orElseThrow(() -> ProjectTaskTypeNotFoundException.forId(id));
    }

    @Transactional
    public Project project(long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> ProjectNotFoundException.forId(projectId));
    }
}
