package ittalents.javaee.controller;

import ittalents.javaee.exceptions.ApiError;
import ittalents.javaee.exceptions.AuthorizationException;
import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@ControllerAdvice
public abstract class AbstractController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ElementNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<Object> handleElementNotFoundException(RuntimeException e) {
        ApiError apiError = new ApiError(e.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), e.getClass().getName());
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleInvalidOperationException(RuntimeException e) {
        ApiError apiError = new ApiError(e.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), e.getClass().getName());
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleConstraintViolationException(RuntimeException e) {
        ApiError error = new ApiError("Invalid form inputs! Please check!", LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), e.getClass().getName());
        return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<Object> handleAuthorizationException(RuntimeException e) {
        ApiError error = new ApiError(e.getMessage(), LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), e.getClass().getName());
        return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<Object> handleSQLException(RuntimeException e) {
        ApiError apiError = new ApiError(e.getMessage(), LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getClass().getName());
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String msg = "Invalid input data! Please check!";
        ApiError error = new ApiError(msg, LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), e.getClass().getName());
        return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    protected ApiError handleGlobalException(Exception e) {
        return new ApiError("Something went wrong... Please try again later", LocalDateTime.now(),
                HttpStatus.SERVICE_UNAVAILABLE.value(), e.getClass().getName());
    }
}
