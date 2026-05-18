package com.seathappens.common.exception;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException exception) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );

        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(URI.create("/errors/not-found"));

        problemDetail.setProperty("code", exception.getErrorCode().code());

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException exception) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Validation Failed");
        problemDetail.setDetail(ErrorCode.VALIDATION_FAILED.message());
        problemDetail.setType(URI.create("/errors/validation"));

        Map<String, String> validationErrors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        validationErrors.put(error.getField(), error.getDefaultMessage())
                );

        problemDetail.setProperty("errors", validationErrors);
        problemDetail.setProperty("code", ErrorCode.VALIDATION_FAILED.code());

        return problemDetail;
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException exception) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );

        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setType(URI.create("/errors/business-rule-violation"));
        problemDetail.setProperty("code", exception.getErrorCode().code());

        return problemDetail;
    }

    @ExceptionHandler(InfrastructureException.class)
    public ProblemDetail handleInfrastructureException(InfrastructureException exception) {

        log.error(exception.getErrorCode().message(), exception);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        problemDetail.setTitle("Infrastructure Error");
        problemDetail.setDetail(exception.getErrorCode().message());
        problemDetail.setType(URI.create("/errors/infrastructure-error"));
        problemDetail.setProperty("code", exception.getErrorCode().code());

        return problemDetail;
    }

    @ExceptionHandler({
            OptimisticLockException.class,
            ObjectOptimisticLockingFailureException.class
    })
    public ProblemDetail handleOptimisticLockingException(Exception exception) {

        log.warn(ErrorCode.CONCURRENT_MODIFICATION.message(), exception);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ErrorCode.CONCURRENT_MODIFICATION.message()
        );

        problemDetail.setTitle("Concurrent Modification");
        problemDetail.setType(URI.create("/errors/concurrent-modification"));
        problemDetail.setProperty("code", ErrorCode.CONCURRENT_MODIFICATION.code());

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception exception) {

        log.error(ErrorCode.INTERNAL_SERVER_ERROR.message(), exception);

        ProblemDetail problemDetail = ProblemDetail.forStatus(
                HttpStatus.INTERNAL_SERVER_ERROR
        );

        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail(ErrorCode.INTERNAL_SERVER_ERROR.message());
        problemDetail.setType(URI.create("/errors/internal-server-error"));

        problemDetail.setProperty("code", ErrorCode.INTERNAL_SERVER_ERROR.code());

        return problemDetail;
    }
}