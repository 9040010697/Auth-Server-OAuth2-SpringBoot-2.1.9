package com.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ServerWebInputException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class AuthGenericExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(HttpClientErrorException exception) {
        return ErrorResponse.builder().message("Error occurred during operation, try again with correct input")
                .statusCode(exception.getStatusCode().toString())
                .time(LocalDateTime.now().toString()).build();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(RuntimeException exception) {
        return ErrorResponse.builder().message("Error occurred during operation, try again with correct input")
                .statusCode(HttpStatus.BAD_REQUEST.toString())
                .time(LocalDateTime.now().toString()).build();
    }

/*    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception exception) {
        return ErrorResponse.builder().message("Error occurred during operation")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .time(LocalDateTime.now().toString()).build();
    }*/


    @ExceptionHandler(value = WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleWebExchangeBindException(WebExchangeBindException exception){
        return ErrorResponse.builder()
                .statusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .time(LocalDateTime.now().toString())
                .message(CollectionUtils.isEmpty(
                        exception.getAllErrors()) ?
                        exception.getMessage() :
                        exception.getAllErrors().get(0).getDefaultMessage())
                .build();
    }


    @ExceptionHandler(value = ServerWebInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleServerWebInputException(ServerWebInputException exception){
        return ErrorResponse.builder()
                .statusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .time(LocalDateTime.now().toString())
                .message(exception.getMessage())
                .build();
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleWebExchangeBindException(MethodArgumentNotValidException exception){
        return ErrorResponse.builder()
                .statusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .time(LocalDateTime.now().toString())
                .message(CollectionUtils.isEmpty(
                        exception.getBindingResult().getAllErrors()) ?
                        exception.getMessage() :
                        exception.getBindingResult().getAllErrors()
                                .get(0).getDefaultMessage())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(ConstraintViolationException exception) {
        return ErrorResponse.builder().message(exception.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.toString())
                .time(LocalDateTime.now().toString()).build();
    }


    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        String[] errs = exception.getMessage().split(";");
        return ErrorResponse.builder()
                .statusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .time(LocalDateTime.now().toString())
                .message((errs != null && errs.length > 0)
                        ? errs[errs.length - 1]
                        : exception.getMessage())
                .build();
    }
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception exception){
        return ErrorResponse.builder()
                .statusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .time(LocalDateTime.now().toString())
                .message("Error occurred " + exception.getMessage())
                .build();
    }





}
