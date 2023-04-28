package com.spring_rest_api.api_paths;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<String> handleMissingRequestHeader(MissingRequestHeaderException e) {
        // Added The last statement so we know its a wrong request.
        // The last statement shouldnt be there in the final release.
        String errorMessage = "There is missing field(s) in the AuthenticationRequest or it is formed improperly. Missing Authorization Error.";

        // Return a ResponseEntity with a custom error message and a 400 Bad Request status
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
