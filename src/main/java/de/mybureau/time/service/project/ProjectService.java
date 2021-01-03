package de.mybureau.time.service.project;

import de.mybureau.time.model.Project;
import de.mybureau.time.model.ProjectTask;
import de.mybureau.time.repository.ProjectRepository;
import de.mybureau.time.repository.ProjectTaskRepository;
import de.mybureau.time.service.client.ClientService;
import de.mybureau.time.service.project.exception.ProjectNotFoundException;
import de.mybureau.time.service.project.exception.ProjectTaskNotFoundException;
import de.mybureau.time.utils.DateTimeHelper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProjectService {

    private final ClientService clientService;
    private final ProjectRepository projectRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final DateTimeHelper dateTimeHelper;

    public ProjectService(ClientService clientService,
                          ProjectRepository projectRepository,
                          ProjectTaskRepository projectTaskRepository,
                          DateTimeHelper dateTimeHelper) {
        this.clientService = clientService;
        this.projectRepository = projectRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.dateTimeHelper = dateTimeHelper;
    }

    @Transactional
    public Project addProject(NewProjectRequest newProjectRequest) {
        final var client = clientService.findClient(newProjectRequest.getClientId());
        final var newProject = new Project();
        newProject.setName(newProjectRequest.getName());
        newProject.setClient(client);
        newProject.setCreatedTs(dateTimeHelper.nowInUtc());
        return projectRepository.save(newProject);
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
        newProjectTask.setCreatedTs(dateTimeHelper.nowInUtc());
        return projectTaskRepository.save(newProjectTask);
    }

    @Transactional
    public ProjectTask findTask(long id) {
        return projectTaskRepository.findById(id)
                .orElseThrow(() -> ProjectTaskNotFoundException.forId(id));
    }

    @Transactional
    public Project project(long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> ProjectNotFoundException.forId(projectId));
    }
}
