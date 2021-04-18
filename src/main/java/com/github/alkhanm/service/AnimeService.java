package com.github.alkhanm.service;

import com.github.alkhanm.domain.Anime;
import com.github.alkhanm.exception.BadRequestException;
import com.github.alkhanm.mapper.AnimeMapper;
import com.github.alkhanm.repository.AnimeRepository;
import com.github.alkhanm.request.AnimePostRequestBody;
import com.github.alkhanm.request.AnimePutRequestBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class AnimeService {
    private final AnimeRepository repository;

    public AnimeService(AnimeRepository repository) {
        this.repository = repository;
    }

    public Page<Anime> listAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<Anime> listAll() {
        return repository.findAll();
    }

    public List<Anime> findByName(String name) {
        return repository.findByName(name);
    }

    public Anime findByIdOrThrowException(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BadRequestException("Anime not found"));
    }

    /* Uma transação só será concluída (no caso, o objeto será salvo)
    ** se todas as operações ocorrerem sem erro algum */
    @Transactional(rollbackOn = Exception.class)
    public Anime save(AnimePostRequestBody animePostRequestBody){
        //Converte de um tipo para outro, e então salva
        return repository.save(AnimeMapper.INSTANCE.toAnime(animePostRequestBody));
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

    public void replace(AnimePutRequestBody animePutRequestBody) {
        Anime animeSaved = findByIdOrThrowException(animePutRequestBody.getId());
        Anime anime = AnimeMapper.INSTANCE.toAnime(animePutRequestBody);
        anime.setId(animeSaved.getId());
        repository.save(anime);
    }
}
