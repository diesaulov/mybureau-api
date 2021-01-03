package de.mybureau.time.service.client;

import static org.apache.commons.lang3.Validate.notEmpty;

public class NewClientRequest {
    private final String name;

    private NewClientRequest(String name) {
        this.name = notEmpty(name, "Client name cannot be empty!");
    }

    public String getName() {
        return name;
    }

    public static NewClientRequestBuilder builder() {
        return new NewClientRequestBuilder();
    }

    public static final class NewClientRequestBuilder {
        private String name;

        private NewClientRequestBuilder() {
        }

        public NewClientRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public NewClientRequest build() {
            return new NewClientRequest(name);
        }
    }
}
