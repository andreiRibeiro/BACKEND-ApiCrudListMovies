package br.com.aaribeiro.listMovies.model;

import lombok.Data;
import java.util.List;

@Data
public class MovieIntervalResponseDTO {
    private List<MovieResponseDTO> min;
    private List<MovieResponseDTO> max;
}
