package br.com.aaribeiro.listMovies.controller;

import br.com.aaribeiro.listMovies.exception.MovieRulesException;
import br.com.aaribeiro.listMovies.entity.MovieEntity;
import br.com.aaribeiro.listMovies.exception.MovieValidationException;
import br.com.aaribeiro.listMovies.model.MovieIntervalResponseDTO;
import br.com.aaribeiro.listMovies.service.MovieService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    MovieService movieService;

    @GetMapping(value = "/core", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get producers with greater and lesser interval between two awards consecutivos.", tags = { "Get Core" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return object empty or with interval min and max."),
            @ApiResponse(code = 500, message = "General processing errors .")
    })
    public ResponseEntity<MovieIntervalResponseDTO> getProducerWithGreaterAndLesserInterval() throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(movieService.getProducerWithGreaterAndLesserInterval());
        } catch (Exception e){
            throw new MovieRulesException(e.getLocalizedMessage());
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get movie.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return movie."),
            @ApiResponse(code = 404, message = "Movie not found."),
            @ApiResponse(code = 500, message = "General processing errors .")
    })
    public ResponseEntity<MovieEntity> getMovie(@PathVariable int id) throws Exception {
        try {
            Optional<MovieEntity> movieEntity = movieService.getMovie(id);
            if (!movieEntity.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.OK).body(movieEntity.get());
        } catch (Exception e) {
            throw new Exception(e.getLocalizedMessage());
        }
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get movies.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return all movies."),
            @ApiResponse(code = 500, message = "General processing errors .")
    })
    public ResponseEntity<List<MovieEntity>> getMovies() throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(movieService.getMovies());
        } catch (Exception e){
            throw new Exception(e.getLocalizedMessage());
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Set movie.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Movie created with successful."),
            @ApiResponse(code = 400, message = "Something wrong with your request.")
    })
    public ResponseEntity<MovieEntity> setMovie(@RequestBody MovieEntity movieEntity) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(movieService.setMovie(movieEntity));
        } catch (Exception e) {
            throw new MovieValidationException(e.getLocalizedMessage());
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update movie.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Update the movie. If the movie does not exist, a new movie will be created."),
            @ApiResponse(code = 400, message = "Something wrong with your request.")
    })
    public ResponseEntity<MovieEntity> updateMovie(@RequestBody MovieEntity movieEntity) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(movieService.updateMovie(movieEntity));
        } catch (Exception e) {
            throw new MovieValidationException(e.getLocalizedMessage());
        }
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Delete movie.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Movie deleted"),
            @ApiResponse(code = 404, message = "Movie not found")
    })
    public ResponseEntity deleteMovie(@PathVariable int id){
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "Import a CSV file.", tags = { "CSV Imports" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Import success"),
            @ApiResponse(code = 500, message = "Import failure")
    })
    public void importCsv(
            @ApiParam(name = "file", value = "Select the file to upload", required = true)
            @RequestPart("file") MultipartFile file) throws Exception {
        try {
            if (file.getContentType().equals("text/csv")){
                movieService.readFileMultipart(file);
            } else {
                throw new Exception("This file is not format CSV");
            }
        } catch (Exception e){
            throw new Exception(e.getLocalizedMessage());
        }
    }
}