package de.mybureau.time.repository;

import de.mybureau.time.model.Project;
import de.mybureau.time.model.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    List<ProjectTask> findByProject(Project project);
}
