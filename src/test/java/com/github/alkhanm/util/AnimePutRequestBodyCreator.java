package com.github.alkhanm.util;

import com.github.alkhanm.request.AnimePutRequestBody;

public class AnimePutRequestBodyCreator {
    public static AnimePutRequestBody create(){
        return AnimePutRequestBody.builder()
                .id(AnimeCreator.createValidAnime().getId())
                .name(AnimeCreator.createValidAnime().getName())
                .build();
    }
}
