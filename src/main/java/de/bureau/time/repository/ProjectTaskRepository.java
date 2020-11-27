package de.bureau.time.repository;

import de.bureau.time.model.Project;
import de.bureau.time.model.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    List<ProjectTask> findByProject(Project project);
}
