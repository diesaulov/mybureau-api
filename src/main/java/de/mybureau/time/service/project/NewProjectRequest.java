package de.mybureau.time.service.project;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;

public class NewProjectRequest {
    private final String name;
    private final long clientId;

    private NewProjectRequest(String name, long clientId) {
        isTrue(clientId > 0, "Client ID must be an integer number greater than 0!");
        this.name = notEmpty(name, "Project name cannot be empty!");
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public long getClientId() {
        return clientId;
    }

    public static NewProjectRequestBuilder builder() {
        return new NewProjectRequestBuilder();
    }

    public static final class NewProjectRequestBuilder {
        private String name;
        private long clientId;

        private NewProjectRequestBuilder() {
        }

        public NewProjectRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public NewProjectRequestBuilder clientId(long clientId) {
            this.clientId = clientId;
            return this;
        }

        public NewProjectRequest build() {
            return new NewProjectRequest(name, clientId);
        }
    }
}
