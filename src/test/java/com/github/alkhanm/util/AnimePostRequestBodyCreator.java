package com.github.alkhanm.util;

import com.github.alkhanm.request.AnimePostRequestBody;

public class AnimePostRequestBodyCreator {

    public static AnimePostRequestBody create(){
        return AnimePostRequestBody.builder()
                .name(AnimeCreator.createAnimeToBeSaved().getName())
                .build();
    }

}
