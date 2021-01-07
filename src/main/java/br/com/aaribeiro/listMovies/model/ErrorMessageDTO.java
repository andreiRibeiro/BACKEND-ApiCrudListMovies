package br.com.aaribeiro.listMovies.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessageDTO {
    private String type;
    private String currentDate;
    private String message;
}
