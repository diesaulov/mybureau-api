package de.bureau.time.service.project.exception;

import de.bureau.time.service.DomainException;

public class ProjectNotFoundException extends DomainException {

    private ProjectNotFoundException(String message) {
        super(ProjectErrors.PROJECT_NOT_FOUND, message);
    }

    public static ProjectNotFoundException forId(long id) {
        return new ProjectNotFoundException(String.format("Project with id '%s' doesn't exist!", id));
    }

    public static ProjectNotFoundException forClientCodeAndProjectCode(String clientCode, String projectCode) {
        return new ProjectNotFoundException(String.format("Cannot find a project with code '%s' for a client with code '%s'!",
                projectCode, clientCode));
    }

    public static ProjectNotFoundException forProjectCode(String projectCode) {
        return new ProjectNotFoundException(String.format("Cannot find a project with code '%s'!", projectCode));
    }
}
