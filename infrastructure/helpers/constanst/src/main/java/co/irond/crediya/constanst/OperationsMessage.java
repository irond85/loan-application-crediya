package co.irond.crediya.constanst;

import lombok.Getter;

@Getter
public enum OperationsMessage {
    REQUEST_RECEIVED("Request received for entity {}"),
    OPERATION_ERROR("Error failed service transactional: {}"),
    SAVE_OK("Loan Application for email {} saved successfully."),
    RESOURCE_CREATED("Resource created successful.");


    private final String message;

    OperationsMessage(String message) {
        this.message = message;
    }
}