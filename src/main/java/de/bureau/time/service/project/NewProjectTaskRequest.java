package de.bureau.time.service.project;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;

public class NewProjectTaskRequest {
    private final String name;
    private final String description;
    private final long projectId;

    private NewProjectTaskRequest(String name, String description, long projectId) {
        isTrue(projectId > 0, "Project ID cannot be negative!");
        this.name = notEmpty(name, "Task name cannot be empty!");
        this.projectId = projectId;
        this.description = notEmpty(description, "Task description cannot be empty!");;
    }

    public String getName() {
        return name;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getDescription() {
        return description;
    }

    public static NewProjectRequestBuilder builder() {
        return new NewProjectRequestBuilder();
    }

    public static final class NewProjectRequestBuilder {
        private String name;
        private long projectId;
        private String description;

        private NewProjectRequestBuilder() {
        }

        public NewProjectRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public NewProjectRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public NewProjectRequestBuilder projectId(long projectId) {
            this.projectId = projectId;
            return this;
        }

        public NewProjectTaskRequest build() {
            return new NewProjectTaskRequest(name, description, projectId);
        }
    }
}
