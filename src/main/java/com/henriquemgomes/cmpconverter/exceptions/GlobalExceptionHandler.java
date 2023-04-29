package com.henriquemgomes.cmpconverter.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.henriquemgomes.cmpconverter.dtos.ExceptionDto;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(CmpConverterException.class)
  protected ResponseEntity<Object> cmpExceptionHandler(CmpConverterException ex) {
    ExceptionDto body = new ExceptionDto(ex.getErrorCode(), ex.getMessage(), ex.getInternalErrorCode());

    return new ResponseEntity<>(body, ex.getStatusCode());
  }
  
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
    MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    Map<String, List<String>> body = new HashMap<>();
    
    List<String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.toList());
    
    body.put("field_validation_errors", errors);
    
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
    HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
      Map<String, String> body = new HashMap<>();
      body.put("field_type_error", ex.getMessage());
      return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> uncaughtExceptionHandler(Exception ex) {
    ExceptionDto body = new ExceptionDto("uncaught error", ex.getMessage(), 900);
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    ex.printStackTrace(pw);
    body.setTrace(sw.toString());
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
