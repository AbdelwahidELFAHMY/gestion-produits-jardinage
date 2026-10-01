package fr.univjardinage.jardinage.controller.advice;

import    fr.univjardinage.jardinage.exception.*;
import    lombok.extern.slf4j.Slf4j;
import    org.springframework.http.HttpStatus;
import    org.springframework.http.ResponseEntity;
import    org.springframework.web.bind.annotation.ExceptionHandler;
import    org.springframework.web.bind.annotation.RestControllerAdvice;
import    org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler{

/**
 * Gestion de ProductNotFoundException
 */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> handleProductNotFoundException(
            ProductNotFoundException ex, WebRequest request){
        log.error(" Produit non trouve :{} ", ex.getMessage());
        Map<String, Object> body = createErrorBody(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                ex.getErrorCode(),
                request
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

/**
 * Gestion de DuplicateProductException
 */
    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<Object> handleDuplicateProductException(
            DuplicateProductException ex, WebRequest request){
        log.error(" Produit en double :{} ", ex.getMessage());
        Map<String, Object> body = createErrorBody(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                ex.getErrorCode(),
                request
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }


/**
 * Gestion de InsufficientStockException
 */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Object> handleInsufficientStockException(
            InsufficientStockException ex, WebRequest request){
        log.error(" Stock insuffisant :{} ", ex.getMessage());
        Map<String, Object> body = createErrorBody(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                ex.getErrorCode(),
                request
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

/**
 * Gestion de InvalidPriceException
 */
    @ExceptionHandler(InvalidPriceException.class)
    public ResponseEntity<Object> handleInvalidPriceException(
        InvalidPriceException ex, WebRequest request){
        log.error(" Prix invalide :{} ", ex.getMessage());
        Map<String, Object> body = createErrorBody(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                ex.getErrorCode(),
                request
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gestion de toutes les autres exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(
            Exception ex, WebRequest request){
        log.error(" Erreur inattendue :{} ", ex.getMessage(), ex);
        Map<String, Object> body = createErrorBody(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur interne s ’ est produite ",
                "INTERNAL_ERROR ",
                request
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Cree le corps de la reponse d ’ erreur
     */
    private Map<String, Object> createErrorBody(
            HttpStatus status,
            String message,
            String errorCode,
            WebRequest request){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("errorCode", errorCode);
        body.put("message", message);
        body.put("path", request.getDescription(false).replace(" uri = ", " "));
        return body;
    }
}