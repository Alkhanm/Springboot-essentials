package com.github.alkhanm.request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimePutRequestBody {

    @Schema(description = "Esse é o id do anime")
    private Long id;

    @NotEmpty
    @Schema(description = "Esse é o nome do anime", example = "Naruto")
    private String name;
}