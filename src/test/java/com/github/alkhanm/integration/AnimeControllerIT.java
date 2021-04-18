package com.github.alkhanm.integration;

import com.github.alkhanm.domain.Anime;
import com.github.alkhanm.domain.MyUser;
import com.github.alkhanm.repository.AnimeRepository;
import com.github.alkhanm.repository.MyUserRepository;
import com.github.alkhanm.request.AnimePostRequestBody;
import com.github.alkhanm.util.AnimeCreator;
import com.github.alkhanm.util.AnimePostRequestBodyCreator;
import com.github.alkhanm.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AnimeControllerIT {
    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

    @Autowired
    private AnimeRepository repository;

    @Autowired
    private MyUserRepository userRepository;


    private static final MyUser USER = MyUser.builder()
            .name("admin supremo")
                .username("Alkham")
                .password("{bcrypt}$2a$10$dPx.5lybP.VFvA8qOUagMeSPkxDmlPZG1NFcTl2u3KlOg6zTlqc4q")
                .authorities("ROLE_USER")
                .build();

    private static final MyUser ADMIN = MyUser.builder()
            .name("admin supremo")
            .username("Feosmur")
            .password("{bcrypt}$2a$10$dPx.5lybP.VFvA8qOUagMeSPkxDmlPZG1NFcTl2u3KlOg6zTlqc4q")
            .authorities("ROLE_USER,ROLE_ADMIN")
            .build();


    @TestConfiguration
    @Lazy
    static class Config {
        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port){
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication(USER.getUsername(), "test");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port){
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication(ADMIN.getUsername(), "test");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("Retorna uma lista de animes dentro de um objeto 'page' ")
    void listAllPageable_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        userRepository.save(USER);

        String expectedName = repository.save(AnimeCreator.createAnimeToBeSaved()).getName();

        PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {}).getBody();

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
        userRepository.save(USER);
        String expectedName = repository.save(AnimeCreator.createAnimeToBeSaved()).getName();

        List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {}).getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName())
                .isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Retorna um anime que possua um certo id")
    void findById_ReturnsAnAnime_WhenSuccessful() {
        userRepository.save(USER);

        long expectedId = repository.save(AnimeCreator.createAnimeToBeSaved()).getId();

        Anime anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedId);

        Assertions.assertThat(anime)
                .isNotNull();

        Assertions.assertThat(anime.getId())
                .isNotNull()
                .isEqualTo(expectedId);
    }

    @Test
    @DisplayName("Retorna uma lista de animes que possuem um certo nome")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        userRepository.save(USER);

        String expectedName = repository.save(AnimeCreator.createAnimeToBeSaved()).getName();

        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> anime = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {}).getBody();

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
        userRepository.save(USER);

        List<Anime> anime = testRestTemplateRoleUser.exchange("/animes/find?name=non_ecziste", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {}).getBody();

        Assertions.assertThat(anime)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Retorna um anime após salva-lo")
    void save_ReturnsAnAnime_WhenSuccessful() {
        userRepository.save(USER);

        AnimePostRequestBody animePost = AnimePostRequestBodyCreator.create();
        ResponseEntity<Anime> animeResponse = testRestTemplateRoleUser.postForEntity("/animes", animePost, Anime.class);

        Assertions.assertThat(animeResponse)
                .isNotNull();

        Assertions.assertThat(animeResponse.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.CREATED);

        Assertions.assertThat(animeResponse.getBody())
                .isNotNull();

        Assertions.assertThat(animeResponse.getBody().getId())
                .isNotNull();
    }

    @Test
    @DisplayName("Substitui um anime por outro")
    void replace_UpdateAnime_WhenSuccessful() {
        userRepository.save(USER);

        Anime animeSaved = repository.save(AnimeCreator.createAnimeToBeSaved());
        animeSaved.setName("new name");

        ResponseEntity<Void> animeResponse =
                testRestTemplateRoleUser.exchange("/animes",
                        HttpMethod.PUT, new HttpEntity<>(animeSaved), Void.class);

        Assertions.assertThat(animeResponse)
                .isNotNull();

        Assertions.assertThat(animeResponse.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Remove um anime")
    void delete_RemovesAnime_WhenSuccessful() {
        userRepository.save(ADMIN);

        Anime animeSaved = repository.save(AnimeCreator.createAnimeToBeSaved());

        ResponseEntity<Void> animeResponse =
                testRestTemplateRoleAdmin.exchange("/animes/admin/{id}",
                        HttpMethod.DELETE, null, Void.class, animeSaved.getId());

        Assertions.assertThat(animeResponse)
                .isNotNull();

        Assertions.assertThat(animeResponse.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Retorna um erro 403 quando o usuário não for ADMIN")
    void delete_Return403_WhenUserIsNotAdmin() {
        userRepository.save(USER);

        Anime animeSaved = repository.save(AnimeCreator.createAnimeToBeSaved());

        ResponseEntity<Void> animeResponse =
                testRestTemplateRoleUser.exchange("/animes/admin/{id}",
                        HttpMethod.DELETE, null, Void.class, animeSaved.getId());

        Assertions.assertThat(animeResponse)
                .isNotNull();

        Assertions.assertThat(animeResponse.getStatusCode())
                .isNotNull()
                .isEqualTo(HttpStatus.FORBIDDEN);
    }
}
