package br.com.aaribeiro.listMovies.core;

import br.com.aaribeiro.listMovies.entity.MovieEntity;
import br.com.aaribeiro.listMovies.model.MovieDTO;
import br.com.aaribeiro.listMovies.model.MovieIntervalResponseDTO;
import br.com.aaribeiro.listMovies.model.MovieResponseDTO;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Rules {

    public MovieIntervalResponseDTO getObjectsResponseApi(List<MovieDTO> objectsApi) throws Exception {
        MovieIntervalResponseDTO objectsResponseApi = new MovieIntervalResponseDTO();

        if (objectsApi.isEmpty()) return new MovieIntervalResponseDTO();
        try {
            Integer intervalMinObjectsApi = objectsApi.stream().mapToInt(MovieDTO::getIntervalMin).min().getAsInt();
            Integer intervalMaxObjectsApi = objectsApi.stream().mapToInt(MovieDTO::getIntervalMax).max().getAsInt();

            List<MovieDTO> objectsApiWithIntervalMin = objectsApi.stream()
                    .filter(movieDTO -> intervalMinObjectsApi.compareTo(movieDTO.getIntervalMin()) == 0)
                    .collect(Collectors.toList());

            List<MovieDTO> objectsApiWithIntervalMax = objectsApi.stream()
                    .filter(movieDTO -> intervalMaxObjectsApi.compareTo(movieDTO.getIntervalMax()) == 0)
                    .collect(Collectors.toList());

            this.formatObjectsForResponseApi(objectsApiWithIntervalMin, "objectsApiWithIntervalMin", objectsResponseApi);
            this.formatObjectsForResponseApi(objectsApiWithIntervalMax, "objectsApiWithIntervalMax", objectsResponseApi);

        } catch (Exception e){
            log.error("Error get objects response api: {}", e.getMessage());
            throw new Exception("Error get objects response api: " + e.getMessage());
        }
        return objectsResponseApi;
    }

    private void formatObjectsForResponseApi(List<MovieDTO> objectsApiWithInterval, String type, MovieIntervalResponseDTO objectsResponseApi) throws Exception {
        try {
            if (type.equals("objectsApiWithIntervalMin")) {
                List<MovieResponseDTO> objectsApiWithIntervalMin = new ArrayList<>();

                objectsApiWithInterval.forEach(movieDTO -> {
                    MovieResponseDTO movieResponseDTO = new MovieResponseDTO();
                    movieResponseDTO.setProducer(movieDTO.getProducer());
                    movieResponseDTO.setInterval(movieDTO.getIntervalMin());
                    movieResponseDTO.setPreviousWin(Integer.parseInt(movieDTO.getRangeMin().split(";")[1]));
                    movieResponseDTO.setFollowingWin(Integer.parseInt(movieDTO.getRangeMin().split(";")[0]));
                    objectsApiWithIntervalMin.add(movieResponseDTO);
                });
                objectsResponseApi.setMin(objectsApiWithIntervalMin);
            }
            if (type.equals("objectsApiWithIntervalMax")) {
                List<MovieResponseDTO> objectsApiWithIntervalMax = new ArrayList<>();

                objectsApiWithInterval.forEach(movieDTO -> {
                    MovieResponseDTO movieResponseDTO = new MovieResponseDTO();
                    movieResponseDTO.setProducer(movieDTO.getProducer());
                    movieResponseDTO.setInterval(movieDTO.getIntervalMax());
                    movieResponseDTO.setPreviousWin(Integer.parseInt(movieDTO.getRangeMax().split(";")[1]));
                    movieResponseDTO.setFollowingWin(Integer.parseInt(movieDTO.getRangeMax().split(";")[0]));
                    objectsApiWithIntervalMax.add(movieResponseDTO);
                });
                objectsResponseApi.setMax(objectsApiWithIntervalMax);
            }
        } catch (Exception e ){
            log.error("Error format objects for response api: {}", e.getMessage());
            throw new Exception("Error format objects for response api: " + e.getMessage());
        }
    }

    public List<MovieDTO> processMinAndMaxIntervalByProducer(List<MovieEntity> listMovies) throws Exception {
        List<MovieDTO> objectWithCalculatedRanges = new ArrayList();
        try {
            for (Map.Entry<String, List<MovieEntity>> entry : listMovies.stream().collect(Collectors.groupingBy(movie -> movie.getProducers())).entrySet()) {
                String producer = entry.getKey();
                List<MovieEntity> movies = entry.getValue();
                List<Integer> years = movies
                        .stream().map(year -> year.getYear()).collect(Collectors.toList())
                        .stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

                if (this.mustHaveAtLeastTwoDates(years)) {
                    Map<String, Integer> rangesCalculated = this.getCalculatedRanges(years);
                    objectWithCalculatedRanges.add(this.getObjectWithCalculatedRanges(
                            producer,
                            rangesCalculated,
                            rangesCalculated.values().stream().mapToInt(Integer::intValue).min().getAsInt(),
                            rangesCalculated.values().stream().mapToInt(Integer::intValue).max().getAsInt()
                            )
                    );
                }
            }
        } catch (Exception e){
            log.error("Error process min and max interval by producer: {}", e.getMessage());
            throw new Exception("Error process min and max interval by producer: " + e.getMessage());
        }
        return objectWithCalculatedRanges;
    }

    private MovieDTO getObjectWithCalculatedRanges(String producer, Map<String, Integer> rangesCalculated, Integer min, Integer max) throws Exception {
        MovieDTO movieDTO = new MovieDTO();
        try {
            rangesCalculated.forEach((key, value) -> {
                movieDTO.setProducer(producer);
                if (value.compareTo(min) == 0) {
                    movieDTO.setRangeMin(key);
                    movieDTO.setIntervalMin(value);
                }
                if (value.compareTo(max) == 0) {
                    movieDTO.setRangeMax(key);
                    movieDTO.setIntervalMax(value);
                }
            });
        } catch (Exception e){
            log.error("Error get object with calculated ranges: {}", e.getMessage());
            throw new Exception("Error get object with calculated ranges: " + e.getMessage());
        }
        return movieDTO;
    }

    private Map<String, Integer> getCalculatedRanges(List<Integer> years) throws Exception {
        Map<String, Integer> datas =  new HashMap();
        try {
            for (int i = 0; i < years.size(); i++) {
                try {
                    int data1 = years.get(i);
                    int data2 = years.get(i + 1);
                    datas.put(data1 + ";" + data2, data1 - data2);
                } catch (IndexOutOfBoundsException e) {
                }
            }
        } catch (Exception e){
            log.error("Error get calculated ranges: {}", e.getMessage());
            throw new Exception("Error get calculated ranges: " + e.getMessage());
        }
         return datas;
    }

    private Boolean mustHaveAtLeastTwoDates(List<Integer> years){
        return years != null && years.size() > 1;
    }

    public List<MovieEntity> separateWhenThereIsMoreThanOneProducer(List<MovieEntity> listMovies) throws Exception {
        List<MovieEntity> listMoviesFiltered = new ArrayList<>();
        try {
            listMovies.forEach(movie -> {

                /*"Ozzie Areu, Will Areu, and Mark E. Swinton" OU "Ozzie Areu, Will Areu and Mark E. Swinton"*/
                if (movie.getProducers().contains(", ")) {
                    Arrays.asList(movie.getProducers().split(", ")).forEach(producerType1 -> {

                        if (producerType1.contains("and ")) {
                            List<String> typeProducers = Arrays.asList(producerType1.split(" and "));

                            /*"and Mark E. Swinton"*/
                            if (typeProducers.size() == 1) {
                                listMoviesFiltered.add(this.setMovieEntityWithNewProducer(movie, producerType1.replace("and ", "")));

                                /*"Will Areu and Mark E. Swinton"*/
                            } else {
                                Arrays.asList(producerType1.split(" and ")).forEach(producerType2 -> {
                                    listMoviesFiltered.add(this.setMovieEntityWithNewProducer(movie, producerType2));
                                });
                            }
                        } else {
                            listMoviesFiltered.add(this.setMovieEntityWithNewProducer(movie, producerType1));
                        }
                    });

                    /*"Will Areu and Mark E. Swinton"*/
                } else if (movie.getProducers().contains(" and ")) {
                    Arrays.asList(movie.getProducers().split(" and ")).forEach(producer -> {
                        listMoviesFiltered.add(this.setMovieEntityWithNewProducer(movie, producer));
                    });
                } else {
                    listMoviesFiltered.add(movie);
                }
            });
        } catch (Exception e) {
            log.error("Error separate when there is more than one producer: {}", e.getMessage());
            throw new Exception("Error get calculated ranges: " + e.getMessage());
        }
        return listMoviesFiltered;
    }

    private MovieEntity setMovieEntityWithNewProducer(MovieEntity movie, String producer){
        MovieEntity movieEntity = MovieEntity.build();
        try {
            movieEntity.setYear(movie.getYear());
            movieEntity.setStudios(movie.getStudios());
            movieEntity.setTitle(movie.getTitle());
            movieEntity.setWinner(movie.getWinner());
            movieEntity.setProducers(producer);
        } catch (Exception e){
            log.error("Error set movie entity with new producer: {}", e.getMessage());
        }
        return movieEntity;
    }
}