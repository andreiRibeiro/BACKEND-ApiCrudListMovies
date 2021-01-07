package br.com.aaribeiro.listMovies.entity;

import br.com.aaribeiro.listMovies.repository.MovieRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Data
@Entity(name = "movies")
public class MovieEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "year")
    @NotNull(message = "Field year cannot be null.")
    private Integer year;

    @Column(name = "title", length = 200)
    @NotNull(message = "Field title cannot be null.")
    private String title;

    @Column(name = "studios", length = 200)
    @NotNull(message = "Field studios cannot be null.")
    private String studios;

    @Column(name = "producers", length = 200)
    @NotNull(message = "Field producers cannot be null.")
    private String producers;

    @Column(name = "winner", length = 50)
    @NotNull(message = "Field winner cannot be null.")
    private String winner;

    @Transient
    @JsonIgnore
    private List<String> movies;

    @Transient
    @JsonIgnore
    private String json;

    private MovieEntity(){};

    public static MovieEntity build(){
        return new MovieEntity();
    }

    private void mustContainAllFields() throws Exception {
        switch (this.movies.size()){
            case 4:
            case 5:
                break;
            default:
                throw new Exception("This movie has more or less fields. Expected between 4 and 5 fields.");
        }
    }

    private void mustHaveNumericalYear() throws Exception {
        try {
            Integer.parseInt(this.movies.get(0));
        } catch (Exception e){
            throw new Exception("The field year must numeric.");
        }
    }

    private void mustContainValueInAllFields() throws Exception {
        String fieldsError = "";
        if (this.movies.get(0).isEmpty()) fieldsError += "year ";
        if (this.movies.get(1).isEmpty()) fieldsError += "title ";
        if (this.movies.get(2).isEmpty()) fieldsError += "studios ";
        if (this.movies.get(3).isEmpty()) fieldsError += "producers";
        if (!fieldsError.isEmpty()) throw new Exception("The filed(s) [" + fieldsError + "] cannot be empty.");
    }

    public MovieEntity validate(String movieCsv){
        try {
            if (movieCsv != null) {
                this.movies = Arrays.asList(movieCsv.split(";"));
                this.mustContainAllFields();
                this.mustContainValueInAllFields();
                this.mustHaveNumericalYear();
            }
        } catch (Exception e){
            this.movies = null;
            log.error("Error validate. This movie contains errors: [ {} ] {}", movieCsv, e.getMessage());
        }
        return this;
    }

    public MovieEntity parse(){
        try {
            if (this.movies != null) {
                this.year = Integer.parseInt(this.movies.get(0));
                this.title = this.movies.get(1);
                this.studios = this.movies.get(2);
                this.producers = this.movies.get(3);
                try {
                    this.winner = this.movies.get(4);
                    if (this.winner.equals("null")) this.winner = "not";

                } catch (ArrayIndexOutOfBoundsException e) {
                    this.winner = "not";
                }
            }
        } catch (Exception e){
            log.error("Error parse. {}", this.movies);
        }
        return this;
    }

    public MovieEntity convertObjectToJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.json = mapper.writeValueAsString(this);
        return this;
    }

    public static String convertObjectToCsv(MovieEntity movieEntity) throws Exception {
        try {
            String fieldsError = "";
            if (movieEntity.getYear() == null) fieldsError += "year ";
            if (movieEntity.getTitle() == null) fieldsError += "title ";
            if (movieEntity.getStudios() == null) fieldsError += "studios ";
            if (movieEntity.getProducers() == null) fieldsError += "producers";
            if (!fieldsError.isEmpty()) throw new Exception("The field(s) [" + fieldsError + "] cannot be null.");
        } catch (Exception e){
            throw new Exception("Error convert. " + e.getMessage());
        }
        return movieEntity.getYear() + ";" + movieEntity.getTitle() + ";" + movieEntity.getStudios() + ";" + movieEntity.getProducers() + ";" + movieEntity.getWinner();
    }

    public MovieEntity save(MovieRepository movieRepository){
        try {
            if (this.movies != null) {
                this.setId(movieRepository.save(this).id);
            }
        } catch (Exception e){
            log.error("Error save: {}", e.getMessage());
        }
        return this;
    }
}