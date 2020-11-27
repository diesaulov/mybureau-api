package de.bureau.time.service.project.exception;

import de.bureau.time.service.DomainException;

public class ProjectTaskTypeNotFoundException extends DomainException {

    private ProjectTaskTypeNotFoundException(String message) {
        super(ProjectErrors.PROJECT_TASK_TYPE_NOT_FOUND, message);
    }

    public static ProjectTaskTypeNotFoundException forCode(String code) {
        return new ProjectTaskTypeNotFoundException(String.format("Cannot find a project task type with code '%s'!", code));
    }

    public static ProjectTaskTypeNotFoundException forId(long id) {
        return new ProjectTaskTypeNotFoundException(String.format("Cannot find a project task type with id '%s'!", id));
    }
}
