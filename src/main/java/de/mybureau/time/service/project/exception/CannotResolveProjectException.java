package de.mybureau.time.service.project.exception;

import de.mybureau.time.service.DomainException;

public class CannotResolveProjectException extends DomainException {

    public CannotResolveProjectException(String projectCode) {
        super(ProjectErrors.PROJECT_NOT_RESOLVED,
                String.format("There is more than one project with code '%s'!", projectCode));
    }
}
