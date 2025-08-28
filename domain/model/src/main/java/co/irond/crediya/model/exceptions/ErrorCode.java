package co.irond.crediya.model.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_LOAN_TYPE(409, "Not exists a loan type with this id."),
    DATABASE_ERROR(500, "An error has occurred while communicating with the database."),
    USER_NOT_FOUND(404, "The user with dni doesn't exists.");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
