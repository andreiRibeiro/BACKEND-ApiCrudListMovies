package br.com.aaribeiro.listMovies.repository;

import br.com.aaribeiro.listMovies.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Integer>{

}
