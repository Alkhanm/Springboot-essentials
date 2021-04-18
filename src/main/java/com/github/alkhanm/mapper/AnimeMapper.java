package com.github.alkhanm.mapper;

import com.github.alkhanm.domain.Anime;
import com.github.alkhanm.request.AnimePostRequestBody;
import com.github.alkhanm.request.AnimePutRequestBody;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

//Serve para converter(mapear) classes de um tipo para outro
@Mapper(componentModel = "spring")
public abstract class AnimeMapper {
    //Retorna uma instância desta classe abstrata para ser acessada
    public static final AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);

    //Converte automaticamente de um tipo para outro (desde que os atributos sejam os mesmos)
    public abstract Anime toAnime(AnimePostRequestBody animePostRequestBody);
    public abstract Anime toAnime(AnimePutRequestBody animePutRequestBody);

}
