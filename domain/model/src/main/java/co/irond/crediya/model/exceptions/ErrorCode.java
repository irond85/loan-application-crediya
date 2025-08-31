package co.irond.crediya.model.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_LOAN_TYPE("BS_NE_001", "Not exists a loan type with this id.", 409),
    USER_NOT_FOUND("BS_NE_002", "The user with dni doesn't exists.", 404),
    DATABASE_ERROR("T_S_001", "An error has occurred while communicating with the database.", 500);

    private final String internCode;
    private final String message;
    private final int httpCode;

    ErrorCode(String internCode, String message, int httpCode) {
        this.internCode = internCode;
        this.message = message;
        this.httpCode = httpCode;
    }

}
