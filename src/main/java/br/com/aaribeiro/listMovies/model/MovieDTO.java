package br.com.aaribeiro.listMovies.model;

import lombok.Data;

@Data
public class MovieDTO {
    private String producer;
    private String rangeMin;
    private String rangeMax;
    private Integer intervalMin;
    private Integer intervalMax;
}
