package br.com.aaribeiro.listMovies;

import br.com.aaribeiro.listMovies.controller.MovieController;
import br.com.aaribeiro.listMovies.controller.MovieControllerAdvice;
import br.com.aaribeiro.listMovies.entity.MovieEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieEntityTest extends ListMovieEntityApplicationTest {

    private MockMvc mockMvc;

    @Autowired
    private MovieController movieController;

    @Autowired
    private MovieControllerAdvice movieControllerAdvice;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.movieController).setControllerAdvice(movieControllerAdvice).build();
    }

    private String getMovieJson() throws JsonProcessingException {
        String movieCsv = "1990;Cruising1;Lorimar Productions, United Artists;Jerry Weintraub;yes";

        return MovieEntity
                .build()
                .validate(movieCsv)
                .parse()
                .convertObjectToJson()
                .getJson();
    }

    private List<String> getMoviesJson(){
        List<String> movies = new ArrayList<>();
        String movieCsv1 = "1990;Cruising1;Lorimar Productions, United Artists;Jerry Weintraub;yes";
        String movieCsv2 = "1991;Cruising2;Lorimar Productions, United Artists;Jerry Weintraub;yes";
        String movieCsv3 = "1990;Can't Stop the Music;Associated Film Distribution;Will Areu;yes";
        String movieCsv4 = "1995;Can't Stop the Music;Associated Film Distribution;Will Areu;yes";

        Arrays.asList(movieCsv1, movieCsv2, movieCsv3, movieCsv4).forEach(movieCsv -> {
            try {
                movies.add(MovieEntity
                        .build()
                        .validate(movieCsv)
                        .parse()
                        .convertObjectToJson()
                        .getJson());
            } catch (Exception e) {}
        });
        return movies;
    }

    @Test
    public void mustRegisterCorrectMovie_ReturnStatusCode201() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.getMovieJson())
        )
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void mustRegisterIncorrectMovie_ReturnStatusCode400() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}")
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void mustUpdateExistingtMovie_ReturnStatusCode200() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                .post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.getMovieJson())).andReturn();

        MovieEntity movieEntity = mapper.readValue(result.getResponse().getContentAsString(), MovieEntity.class);
        movieEntity.setProducers("New producer");

        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(movieEntity))
        )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void mustConsultMovieThatExists_ReturnStatusCode200() throws Exception {
        /*POST movie*/
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.getMovieJson())
        );

        /*GET movie*/
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/movies/1")
        )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void mustConsultMovieThatDoesNotExists_ReturnStatusCode404() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/movies/99")
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void mustDeleteAnMovieThatExists_ReturnStatusCode200() throws Exception {
        /*POST movie*/
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.getMovieJson())
        );

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.getMovieJson())
        );

        /*GET movie*/
        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/movies/2")
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void mustDeleteMovieThatDoesNotExist_RetornarStatusCode404() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/movies/99")
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void mustGetProducerWithShorterInterval_ReturnNameProducer() throws Exception {
        /*POST movies*/
        this.getMoviesJson().forEach(movieJson -> {
            try {
                this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson)
                );
            } catch (Exception e) {}
        });

        /*GET objects*/
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/movies/core")
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.min[0].producer").value("Jerry Weintraub"));
    }

    @Test
    public void mustGetProducerWithLongerInterval_ReturnNameProducer() throws Exception {
        /*POST movies*/
        this.getMoviesJson().forEach(movieJson -> {
            try {
                this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson)
                );
            } catch (Exception e) {}
        });

        /*GET objects*/
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/movies/core")
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.max[0].producer").value("Will Areu"));
    }
}