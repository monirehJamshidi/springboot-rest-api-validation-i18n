package org.j2os.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.j2os.common.dto.ErrorResponse;
import org.j2os.common.dto.ValidationError;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ErrorHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationError(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ){
        List<ValidationError> fieldErrors =ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new ValidationError(
                        err.getField(),
                        err.getDefaultMessage()
//                        messageSource.getMessage(
//                                err,
//                                LocaleContextHolder.getLocale()
//                        )//err.getDefaultMessage()
                ))
                .toList();

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError("VALIDATION_ERROR");
        response.setErrorCode(errorCode.getCode());
        response.setMessage(
                messageSource.getMessage(
                        "validation.failed",//errorCode.getMessage(),
                        null,
                        LocaleContextHolder.getLocale()
                )
                );
        response.setPath(request.getRequestURI());
        response.setValidationErrors(fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> sqlError(
            SQLException ex,
            HttpServletRequest request){

        log.error("Database error", ex);

        ErrorCode errorCode = ErrorCode.DATABASE_ERROR;

        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setError(errorCode.name());
        response.setErrorCode(errorCode.getCode());
        response.setMessage(errorCode.getMessage());
        response.setPath(request.getRequestURI());
        response.setValidationErrors(null);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> methodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
        response.setError("METHOD_NOT_ALLOWED");
        response.setErrorCode(405);
        response.setMessage(ex.getMessage());
        response.setPath(request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> genericError(
            Exception ex,
            HttpServletRequest request){

        log.error("Unexpected error", ex);

        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;

        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setError(errorCode.name());
        response.setErrorCode(errorCode.getCode());
        response.setMessage(errorCode.getMessage());
        response.setPath(request.getRequestURI());
        response.setValidationErrors(null);


        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}