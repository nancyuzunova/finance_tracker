package ittalents.javaee.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ElementNotFoundException.class})
    protected ResponseEntity<Object> handleElementNotFoundException(RuntimeException e) {
        ApiError apiError = new ApiError(e.getMessage());
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {InvalidOperationException.class, ConstraintViolationException.class})
    protected ResponseEntity<Object> handleInvalidOperationException(RuntimeException e) {
        ApiError apiError = new ApiError(e.getMessage());
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
