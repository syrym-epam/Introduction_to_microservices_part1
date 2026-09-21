package com.microservice.song_service.exceptions;


import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.microservice.song_service.dto.ErrorResponseDTO;
import com.microservice.song_service.dto.ErrorValidationResponseDTO;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> dataNotFound(DataNotFoundException ex) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
            "" + HttpStatus.NOT_FOUND.value(),
            ex.getMessage()
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorValidationResponseDTO> validationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().stream().forEach(error -> {
            errors.put(((FieldError) error).getField(), error.getDefaultMessage());
        });

        ErrorValidationResponseDTO errorResponseDTO = new ErrorValidationResponseDTO(
            "" + HttpStatus.BAD_REQUEST.value(),
            "Validation error",
            errors
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MetaDataExistExceptions.class)
    public ResponseEntity<ErrorResponseDTO> metaDataExistException(MetaDataExistExceptions ex) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
            "" + HttpStatus.CONFLICT.value(),
            ex.getMessage()
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> argumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
            "" + HttpStatus.BAD_REQUEST.value(),
            "Invalid value '%s' for ID. Must be a positive integer".formatted(ex.getValue())
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> validationControllerLevel(ConstraintViolationException ex) {

        String errorMessage = "";
        String errorValue = "";

        if (!ex.getConstraintViolations().isEmpty()) {
            var error = ex.getConstraintViolations().iterator().next();
            errorMessage = error.getMessage();

            var type = error.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
            errorValue = switch (type) {
                case "Size" -> String.valueOf(error.getInvalidValue().toString().length());
                case "Pattern" -> error.getInvalidValue().toString()
                        .replaceAll("(^|,)\\s*\\d+\\s*(?=,|$)", "")
                        .replaceAll("^,|,$", "")
                        .replaceAll(",{2,}", ",");
                default -> error.getInvalidValue().toString();
            };
        }

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                "" + HttpStatus.BAD_REQUEST.value(),
                errorMessage.formatted(errorValue));

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }
}