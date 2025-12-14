package org.j2os.common;

public enum ErrorCode {
    VALIDATION_ERROR(703, "Validation failed"),
    DATABASE_ERROR(700, "Database error"),
    UNKNOWN_ERROR(900, "Unexpected error");

    private final int code;
    private final String message;

    ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
