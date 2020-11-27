package de.bureau.time.service.project.exception;

import de.bureau.time.service.DomainException;

public class CannotResolveProjectException extends DomainException {

    public CannotResolveProjectException(String projectCode) {
        super(ProjectErrors.PROJECT_NOT_RESOLVED,
                String.format("There is more than one project with code '%s'!", projectCode));
    }
}
