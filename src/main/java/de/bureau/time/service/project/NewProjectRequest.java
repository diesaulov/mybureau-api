package de.bureau.time.service.project;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;

public class NewProjectRequest {
    private final String name;
    private final String code;
    private final long userId;
    private final String clientCode;

    private NewProjectRequest(String name, String code, String clientCode, long userId) {
        isTrue(userId > 0, "User ID must be an integer number greater than 0!");
        this.name = notEmpty(name, "Project name cannot be empty!");
        this.code = notEmpty(code, "Project code cannot be empty!");
        this.clientCode = notEmpty(clientCode, "Client code cannot be empty!");
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public long getUserId() {
        return userId;
    }

    public String getClientCode() {
        return clientCode;
    }

    public static NewProjectRequestBuilder builder() {
        return new NewProjectRequestBuilder();
    }

    public static final class NewProjectRequestBuilder {
        private String name;
        private String code;
        private String clientCode;
        private long userId;

        private NewProjectRequestBuilder() {
        }

        public NewProjectRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public NewProjectRequestBuilder code(String code) {
            this.code = code;
            return this;
        }

        public NewProjectRequestBuilder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public NewProjectRequest build() {
            return new NewProjectRequest(name, code, clientCode, userId);
        }
    }
}
