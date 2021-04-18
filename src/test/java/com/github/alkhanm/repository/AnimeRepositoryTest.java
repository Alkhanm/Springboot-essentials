package com.github.alkhanm.repository;

import com.github.alkhanm.domain.Anime;
import com.github.alkhanm.util.AnimeCreator;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@Log4j2
@DisplayName("Testes para: AnimeRepository")
class AnimeRepositoryTest {
    @Autowired
    private AnimeRepository repository;

    @Test
    @DisplayName("Persiste o anime quando a operação for bem-sucedida")
    void create_PersistAnime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.repository.save(animeToBeSaved);

        //Verifica/Assegura que o anime salvo não está nulo
        Assertions.assertThat(animeSaved)
                .isNotNull();
        //Verifica/Assegura que o id do anime salvo não está vazio
        Assertions.assertThat(animeSaved.getId())
                .isNotNull();
        //Verifica/Assegura que o nome do anime salvo é igual ao do anime a ser salvo
        Assertions.assertThat(animeSaved.getName())
                .isEqualTo(animeToBeSaved.getName());
    }

    @Test
    @DisplayName("Lança uma exceção caso o anime esteja com nome vazio ao tentar salvar")
    void create_throw_ConstraintValidationException_WhenNameIsEmpty() {
        Anime anime = new Anime();
        /* Assertions.assertThatThrownBy(() -> this.repository.save(anime))
         *    .isInstanceOf(ConstraintViolationException.class); */
        Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> this.repository.save(anime))
                .withMessageContaining("The anime name cannot be empty");
    }


    @Test
    @DisplayName("Atualiza o anime quando a operação for bem-sucedida")
    void update_Anime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.repository.save(animeToBeSaved);
        animeSaved.setName("Overlord");
        Anime animeUpdated = this.repository.save(animeSaved);

        Assertions.assertThat(animeUpdated)
                .isNotNull();

        Assertions.assertThat(animeUpdated.getId())
                .isNotNull();

        Assertions.assertThat(animeUpdated.getName())
                .isEqualTo(animeSaved.getName());
    }


    @Test
    @DisplayName("Remove o anime quando a operação for bem-sucedida")
    void delete_RemoveAnime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.repository.save(animeToBeSaved);

        this.repository.delete(animeSaved);

        Optional<Anime> animeOptional = this.repository.findById(animeSaved.getId());

        Assertions.assertThat(animeOptional)
                .isEmpty();
    }

    @Test
    @DisplayName("Retorna uma lista de animes quando a operação for bem-sucedida")
    void read_findByName_ReturnsListOfAnimes_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.repository.save(animeToBeSaved);

        String name = animeSaved.getName();

        List<Anime> animes = this.repository.findByName(name);

        Assertions.assertThat(animes)
                .isNotEmpty()
                .contains(animeSaved);
    }

    @Test
    @DisplayName("Retorna uma lista vazia de animes quando não houver nenhum anime com esse nome")
    void read_findByName_ReturnsEmptyListOfAnimes_WhenAnimeIsNotFound() {
        List<Anime> animes = this.repository.findByName("does_not_exist");

        Assertions.assertThat(animes)
                .isEmpty();
    }
}