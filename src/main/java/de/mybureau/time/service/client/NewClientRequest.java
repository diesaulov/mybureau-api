package de.mybureau.time.service.client;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;

public class NewClientRequest {
    private final String name;
    private final String code;
    private final long userId;

    private NewClientRequest(String name, String code, long userId) {
        isTrue(userId > 0, "User ID must be an integer number greater than 0!");
        this.name = notEmpty(name, "Client name cannot be empty!");
        this.code = notEmpty(code, "Client code cannot be empty!");
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

    public static NewClientRequestBuilder builder() {
        return new NewClientRequestBuilder();
    }

    public static final class NewClientRequestBuilder {
        private String name;
        private String code;
        private long userId;

        private NewClientRequestBuilder() {
        }

        public NewClientRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public NewClientRequestBuilder code(String code) {
            this.code = code;
            return this;
        }

        public NewClientRequestBuilder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public NewClientRequest build() {
            return new NewClientRequest(name, code, userId);
        }
    }
}
