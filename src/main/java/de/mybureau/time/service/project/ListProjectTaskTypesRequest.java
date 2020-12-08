package de.mybureau.time.service.project;

import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;

public class ListProjectTaskTypesRequest {
    private final long userId;
    private final String projectCode;
    private final String clientCode;

    private ListProjectTaskTypesRequest(long userId, String projectCode, String clientCode) {
        isTrue(userId > 0, "User ID must be an integer number greater than 0!");
        this.userId = userId;
        this.projectCode = notEmpty(projectCode, "Project code cannot be empty!");
        this.clientCode = clientCode;
    }

    public long getUserId() {
        return userId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public String getClientCode() {
        return clientCode;
    }

    public boolean hasClientCode() {
        return !StringUtils.isEmpty(clientCode);
    }

    public static ListProjectTaskTypesRequestBuilder builder() {
        return new ListProjectTaskTypesRequestBuilder();
    }

    public static final class ListProjectTaskTypesRequestBuilder {
        private long userId;
        private String projectCode;
        private String clientCode;

        private ListProjectTaskTypesRequestBuilder() {
        }

        public ListProjectTaskTypesRequestBuilder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public ListProjectTaskTypesRequestBuilder projectCode(String projectCode) {
            this.projectCode = projectCode;
            return this;
        }

        public ListProjectTaskTypesRequestBuilder clientCode(String clientCode) {
            this.clientCode = clientCode;
            return this;
        }

        public ListProjectTaskTypesRequest build() {
            return new ListProjectTaskTypesRequest(userId, projectCode, clientCode);
        }
    }
}
