package br.com.aaribeiro.listMovies.service;

import br.com.aaribeiro.listMovies.core.Rules;
import br.com.aaribeiro.listMovies.entity.MovieEntity;
import br.com.aaribeiro.listMovies.model.MovieDTO;
import br.com.aaribeiro.listMovies.repository.MovieRepository;
import br.com.aaribeiro.listMovies.model.MovieIntervalResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public void readFileMultipart(MultipartFile file) throws Exception {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(file.getBytes());
            Arrays.asList(baos.toString().split("\n")).forEach(movie -> {
                if (!movie.split(";")[0].equals("year")){
                    MovieEntity
                            .build()
                            .validate(movie)
                            .parse()
                            .save(movieRepository);
                }
            });
        } catch (Exception e) {
            log.error("Error read MultipartFile: {}", e.getMessage());
            throw new Exception("Error read MultipartFile: " + e.getMessage());
        }
    }

    public void readFileCsv(String fileCsv){
        try {
            Files.lines(Paths.get(fileCsv))
                    .skip(1)
                    .forEach(movie -> {
                        MovieEntity
                                .build()
                                .validate(movie)
                                .parse()
                                .save(movieRepository);
                    });
        } catch (IOException e) {
            log.error("Error read file CSV: {}", e.getMessage());
        }
    }

    public MovieIntervalResponseDTO getProducerWithGreaterAndLesserInterval() throws Exception {
        try {
            Rules rules = new Rules();
            List<MovieDTO> objectsResponseApi = rules.processMinAndMaxIntervalByProducer(rules.separateWhenThereIsMoreThanOneProducer(this.getMovies()));
            return rules.getObjectsResponseApi(objectsResponseApi);
        } catch (Exception e){
            log.error("Error get producer with greater and lesser interval: {}", e.getMessage());
            throw new Exception("Error get producer with greater and lesser interval: " + e.getMessage());
        }
    }

    public Optional<MovieEntity> getMovie(int id){
        return movieRepository.findById(id);
    }

    public List<MovieEntity> getMovies(){
        return movieRepository.findAll();
    }

    public MovieEntity updateMovie(MovieEntity movieEntity) {
        return movieRepository.save(movieEntity);
    }

    public void deleteMovie(int id) throws Exception {
        try {
            movieRepository.deleteById(id);
        } catch (Exception e){
            throw new Exception();
        }
    }

    public MovieEntity setMovie(MovieEntity movieJson) throws Exception {
        return MovieEntity
                .build()
                .validate(MovieEntity.convertObjectToCsv(movieJson))
                .parse()
                .save(movieRepository);
    }
}