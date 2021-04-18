package com.github.alkhanm.util;

import com.github.alkhanm.domain.Anime;

public class AnimeCreator {

    public static Anime createAnimeToBeSaved(){
        return Anime.builder()
                .name("Bleach")
                .build();
    }

    public static Anime createValidAnime(){
        return Anime.builder()
                .id(1L)
                .name("Bleach")
                .build();
    }

    public static Anime createValidUpdatedAnime(){
        return Anime.builder()
                .id(1L)
                .name("One Piece")
                .build();
    }
}
