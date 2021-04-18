package com.github.alkhanm.controller;
import com.github.alkhanm.domain.Anime;
import com.github.alkhanm.request.AnimePostRequestBody;
import com.github.alkhanm.request.AnimePutRequestBody;
import com.github.alkhanm.service.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/animes")
@Log4j2
public class AnimeController {
    private final AnimeService service;

    public AnimeController(AnimeService service) {
        //Realiza a injeção de dependência automatica de um "bean"
        this.service = service;
    }

    //SWAGGER: Parameter(hidden = true), esconde da documentação do Swagger algum parâmetro
    //Parameter: ParameterObject, organiza os parâmetros na documentação
    @GetMapping // localhost:8080/animes?page=0&size=3&sort=id,DESC
    @Operation(summary = "Retorna uma lista paginada de animes",
            description = "O tamanho padrão da lista é de 20 elementos, use o parâmetro 'size' para mudar isso",
            tags = {"Listar"})
    public ResponseEntity<Page<Anime>> list(@ParameterObject Pageable pageable){
      return ResponseEntity.ok(service.listAll(pageable));
    }

    @GetMapping(path = "/all")
    @Operation(summary = "Retorna uma lista com todos os animes", tags = {"Listar"})
    public ResponseEntity<List<Anime>> list(){
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Busca um anime através do id", tags = {"Buscar"})
    public ResponseEntity<Anime> findById(@PathVariable long id){
        return ResponseEntity.ok(service.findByIdOrThrowException(id));
    }
    @GetMapping(path = "by-id/{id}")
    @Operation(summary = "Busca um anime através do id", tags = {"Buscar"})
    public ResponseEntity<Anime> findByIdAuthenticated(@PathVariable long id,
                                                       @AuthenticationPrincipal UserDetails userDetails){
        System.out.println("Usuário autenticado: " + userDetails);
        return ResponseEntity.ok(service.findByIdOrThrowException(id));
    }

    @GetMapping(path = "/find")
    @Operation(summary = "Busca animes através do nome", tags = {"Buscar"})
    public ResponseEntity<List<Anime>> findByName(@RequestParam String name){
        return ResponseEntity.ok(service.findByName(name));
    }

    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')") //Antes de executar, verifica se o usuário possui a autorização necessária
    public ResponseEntity<Anime> save(@RequestBody @Valid AnimePostRequestBody anime){
        return new ResponseEntity<>(service.save(anime), HttpStatus.CREATED);
    }


    @DeleteMapping(path = "admin/{id}")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "204", description = "Operação bem-sucedida"),
            @ApiResponse(responseCode = "400", description = "Quando o anime não existe no banco de dados")
    })
    public ResponseEntity<Void> delete(@PathVariable long id){
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping
    public ResponseEntity<Void> replace(@RequestBody AnimePutRequestBody anime){
        service.replace(anime);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
