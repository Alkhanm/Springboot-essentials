package com.github.alkhanm.controller;

import com.github.alkhanm.domain.Anime;
import com.github.alkhanm.request.AnimePostRequestBody;
import com.github.alkhanm.request.AnimePutRequestBody;
import com.github.alkhanm.service.AnimeService;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    @InjectMocks //Injeta a classe que será testada
    private AnimeController controller;

    @Mock //Injeta as classes utilizadas pela classe testada
    private AnimeService service;

    @BeforeEach
        //Executa antes de qualquer operação
    void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));

        // Quando o método listAll for chamado com algum argumento, retornará o objeto "animePage"
        BDDMockito.when(service.listAll(ArgumentMatchers.any()))
                .thenReturn(animePage);

        //Quando não receber argumentos
        BDDMockito.when(service.listAll())
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(service.findByIdOrThrowException(ArgumentMatchers.anyLong()))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.when(service.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(service.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing().when(service)
                .replace(ArgumentMatchers.any(AnimePutRequestBody.class));

        BDDMockito.doNothing().when(service)
                .delete(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("Retorna uma lista de animes dentro de um objeto 'page' ")
    void listAllPageable_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();

        Page<Anime> animePage = controller.list(null).getBody();

        Assertions.assertThat(animePage)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animePage.toList().get(0).getName())
                .isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Retorna uma lista com todos os animes")
    void listAll_ReturnsListOfAnimes_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = controller.list().getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty();

        Assertions.assertThat(animes.get(0).getName())
                .isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Retorna um anime que possua um certo id")
    void findById_ReturnsAnAnime_WhenSuccessful() {
        long expectedId = AnimeCreator.createValidAnime().getId();

        Anime anime = controller.findById(1).getBody();

        Assertions.assertThat(anime)
                .isNotNull();

        Assertions.assertThat(anime.getId())
                .isNotNull()
                .isEqualTo(expectedId);
    }

    @Test
    @DisplayName("Retorna uma lista de animes que possuem um certo nome")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();

        List<Anime> anime = controller.findByName("").getBody();

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
        BDDMockito.when(service.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> anime = controller.findByName("").getBody();

        Assertions.assertThat(anime)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Retorna um anime após salva-lo")
    void save_ReturnsAnAnime_WhenSuccessful() {
        AnimePostRequestBody bodyRequest = AnimePostRequestBodyCreator.create();
        Anime anime = controller.save(bodyRequest).getBody();

        Assertions.assertThat(anime)
                .isNotNull()
                .isEqualTo(AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("Substitui um anime por outro")
    void replace_UpdateAnime_WhenSuccessful() {
        AnimePutRequestBody animePutRequestBody = AnimePutRequestBodyCreator.create();

        Assertions.assertThatCode(() -> controller.replace(animePutRequestBody))
                .doesNotThrowAnyException();

        Assertions.assertThat(controller.replace(animePutRequestBody).getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Remove um anime")
    void delete_RemovesAnime_WhenSuccessful() {
        Assertions.assertThatCode(() -> controller.delete(1L))
                .doesNotThrowAnyException();

        Assertions.assertThat(controller.delete(1L).getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

}