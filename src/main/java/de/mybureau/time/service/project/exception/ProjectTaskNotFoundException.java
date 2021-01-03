package de.mybureau.time.service.project.exception;

import de.mybureau.time.service.DomainException;

public class ProjectTaskNotFoundException extends DomainException {

    private ProjectTaskNotFoundException(String message) {
        super(ProjectErrors.PROJECT_TASK_TYPE_NOT_FOUND, message);
    }

    public static ProjectTaskNotFoundException forCode(String code) {
        return new ProjectTaskNotFoundException(String.format("Cannot find a project task type with code '%s'!", code));
    }

    public static ProjectTaskNotFoundException forId(long id) {
        return new ProjectTaskNotFoundException(String.format("Cannot find a project task type with id '%s'!", id));
    }
}
