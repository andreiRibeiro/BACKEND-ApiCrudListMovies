package br.com.aaribeiro.listMovies.controller;

import br.com.aaribeiro.listMovies.exception.MovieRulesException;
import br.com.aaribeiro.listMovies.model.ErrorMessageDTO;
import br.com.aaribeiro.listMovies.exception.MovieValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDate;

@RestControllerAdvice
public class MovieControllerAdvice  {

    @ExceptionHandler(value = {MovieValidationException.class})
    public ResponseEntity<Object> movieValidationException(MovieValidationException e){
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO("Exception found during validation.", LocalDate.now().toString(), e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageDTO);
    }

    @ExceptionHandler(value = {MovieRulesException.class})
    public ResponseEntity<Object> rulesException(MovieRulesException e){
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO("Exception found during processing.", LocalDate.now().toString(), e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessageDTO);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> genericException(Exception e){
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO("Exception generic.", LocalDate.now().toString(), e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessageDTO);
    }
}
