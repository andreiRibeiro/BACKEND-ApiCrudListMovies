package br.com.aaribeiro.listMovies.model;

import lombok.Data;

@Data
public class MovieResponseDTO {
    private String producer;
    private Integer interval;
    private Integer previousWin;
    private Integer followingWin;
}
