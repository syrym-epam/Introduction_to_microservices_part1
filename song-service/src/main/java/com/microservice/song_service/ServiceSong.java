package com.microservice.song_service;


import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.microservice.song_service.dto.EntitySongDTO;
import com.microservice.song_service.exceptions.DataNotFoundException;
import com.microservice.song_service.exceptions.MetaDataExistExceptions;

import jakarta.transaction.Transactional;

@Service 
public class ServiceSong {

    @Value("${resource-service.url}")
    String resourceServiceUrl;

    private RepositorySong repositorySong;
    private final RestTemplate restTemplate;

    public ServiceSong(RepositorySong repositorySong) {
        this.repositorySong = repositorySong;
        this.restTemplate = new RestTemplate();
    }

    public Long saveSong(EntitySongDTO entityDto) {
        if (repositorySong.existsById(entityDto.id)) {
            throw new MetaDataExistExceptions("Metadata with the %d ID already exists".formatted(entityDto.id));
        }

        EntitySong entitySong = new EntitySong();
        entitySong.setId(entityDto.id);
        entitySong.setAlbum(entityDto.album);
        entitySong.setArtist(entityDto.artist);
        entitySong.setName(entityDto.name);
        entitySong.setDuration(entityDto.duration);
        entitySong.setYear(entityDto.year);

        repositorySong.save(entitySong);

        return entityDto.getId();
    }

    @Transactional
    public EntitySongDTO getSongById(Long id) {
        Boolean exist = restTemplate.getForObject(resourceServiceUrl + "/resources/exist/" + id, Boolean.class);

        if (!exist)
            throw new DataNotFoundException("Song metadata for ID=%d not found".formatted(id));

        EntitySong entitySong = repositorySong.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Song metadata for ID=%d not found".formatted(id)));

        EntitySongDTO entitySongDTO = EntitySongDTO
                .builder()
                .id(entitySong.getId())
                .album(entitySong.getAlbum())
                .artist(entitySong.getArtist())
                .name(entitySong.getName())
                .duration(entitySong.getDuration())
                .year(entitySong.getYear())
                .build();

        return entitySongDTO;
    }

    public List<EntitySongDTO> getListSong() {
        return repositorySong.findAll().stream().map(EntitySongDTO::fromEntitySong).toList();
    }

    public void deleteAllSongById(List<Long> ids) {
        repositorySong.deleteAllById(ids); 
    }
}