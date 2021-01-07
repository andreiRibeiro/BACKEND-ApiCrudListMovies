package br.com.aaribeiro.listMovies;

import br.com.aaribeiro.listMovies.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class ListMoviesApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext contexto = SpringApplication.run(ListMoviesApplication.class, args);
		MovieService movieService = contexto.getBean(MovieService.class);

		switch (args.length) {
			case 0:
				log.info("No CSV load file was identified as a parameter.");
			break;

			case 1:
				movieService.readFileCsv(args[0]);
			break;

			default:
				log.info("The system allows only one CSV load file as a parameter.");
			break;
		}
	}
}
