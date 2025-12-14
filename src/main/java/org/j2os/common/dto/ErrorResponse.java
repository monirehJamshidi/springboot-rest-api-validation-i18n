package org.j2os.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;              // HTTP status (400, 500, ...)
    private String error;            // VALIDATION_ERROR, DATABASE_ERROR
    private int errorCode;           // custom app code
    private String message;          // general message
    private String path;              // request path
    private List<ValidationError> validationErrors; // optional
}
