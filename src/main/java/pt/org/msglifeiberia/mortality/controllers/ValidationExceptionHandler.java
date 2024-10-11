package pt.org.msglifeiberia.mortality.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import pt.org.msglifeiberia.mortality.exceptions.InvalidException;
import pt.org.msglifeiberia.mortality.exceptions.NotFoundException;
import pt.org.msglifeiberia.mortality.models.ExceptionErrorsResponse;
import pt.org.msglifeiberia.mortality.models.ExceptionResponse;

import java.util.*;

@ControllerAdvice
@RestController
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(),
                ex.getMessage(),
                request.getDescription(false),
                HttpStatus.NOT_FOUND.getReasonPhrase());

        return new ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidException.class)
    public final ResponseEntity<ExceptionResponse> handleInvalidException(InvalidException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(),
                ex.getMessage(),
                request.getDescription(false),
                HttpStatus.PRECONDITION_FAILED.getReasonPhrase());

        return new ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.PRECONDITION_FAILED);
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionErrorsResponse> handleNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> errors = new ArrayList<>();

        ex.getAllErrors().forEach(er -> errors.add(er.getDefaultMessage()));

        ExceptionErrorsResponse exceptionErrorsResponse = new ExceptionErrorsResponse(new Date(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), errors);
        return new ResponseEntity<>(exceptionErrorsResponse, HttpStatus.BAD_REQUEST);
    }

    /*

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> notValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();

        ex.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));

        Map<String, List<String>> result = new HashMap<>();
        result.put("errors", errors);

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

     */


}
