package com.github.alkhanm.service;

import com.github.alkhanm.domain.Anime;
import com.github.alkhanm.exception.BadRequestException;
import com.github.alkhanm.repository.AnimeRepository;
import com.github.alkhanm.request.AnimePostRequestBody;
import com.github.alkhanm.util.AnimeCreator;
import com.github.alkhanm.util.AnimePostRequestBodyCreator;
import com.github.alkhanm.util.AnimePutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DisplayName("Testes para: AnimeService")
class AnimeServiceTest {

    @InjectMocks //Injeta a classe que será testada
    private AnimeService service;

    @Mock //Injeta as classes utilizadas pela classe testada
    private AnimeRepository repository;

    @BeforeEach//Executa antes de qualquer operação
    void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        // Quando o método listAll for chamado com algum argumento, retornará o objeto "animePage"
        BDDMockito.when(repository.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(animePage);

        //Quando não receber argumentos
        BDDMockito.when(repository.findAll())
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(repository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(repository.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(repository.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing().when(repository)
                .delete(ArgumentMatchers.any(Anime.class));
    }

    @Test
    @DisplayName("Retorna uma lista de animes dentro de um objeto 'page' ")
    void listAllPageable_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();

        Page<Anime> animePage = service.listAll(PageRequest.of(1,1));

        Assertions.assertThat(animePage)
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animePage.toList().get(0).getName())
                .isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Retorna uma lista com todos os animes")
    void listAll_ReturnsListOfAnimes_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = service.listAll();

        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty();

        Assertions.assertThat(animes.get(0).getName())
                .isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Retorna um anime que possua um certo id ou então lança uma exceção")
    void findByIdOrThrowException_ReturnsAnAnime_WhenSuccessful() {
        long expectedId = AnimeCreator.createValidAnime().getId();

        Anime anime = service.findByIdOrThrowException(1);

        Assertions.assertThat(anime)
                .isNotNull();

        Assertions.assertThat(anime.getId())
                .isNotNull()
                .isEqualTo(expectedId);
    }

    @Test
    @DisplayName("Lança uma BadRequestException quando nem um anime for encontrado")
    void findByIdOrThrowException_ThrowsBadRequestException_WhenAnimeIsNotFound() {
        BDDMockito.when(repository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> this.service.findByIdOrThrowException(1L));
    }

    @Test
    @DisplayName("Retorna uma lista de animes que possuem um certo nome")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();

        List<Anime> anime = service.findByName("");

        Assertions.assertThat(anime)
                .isNotNull()
                .isNotEmpty();

        Assertions.assertThat(anime.get(0).getName())
                .isNotNull()
                .isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Retorna uma lista vazia de animes se nem um deles possuir certo nome")
    void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {
        BDDMockito.when(repository.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animes = service.findByName("");

        Assertions.assertThat(animes)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Retorna um anime após salva-lo")
    void save_ReturnsAnAnime_WhenSuccessful() {
        AnimePostRequestBody bodyRequest = AnimePostRequestBodyCreator.create();
        Anime anime = service.save(bodyRequest);

        Assertions.assertThat(anime)
                .isNotNull()
                .isEqualTo(AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("Substitui um anime por outro")
    void replace_UpdateAnime_WhenSuccessful() {
        Assertions.assertThatCode(() -> service.replace(AnimePutRequestBodyCreator.create()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Remove um anime")
    void delete_RemovesAnime_WhenSuccessful() {
        Assertions.assertThatCode(() -> service.delete(1L))
                .doesNotThrowAnyException();
    }

}