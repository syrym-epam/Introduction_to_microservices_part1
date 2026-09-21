package com.microservice.song_service;


import java.util.Arrays;
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
            throw new MetaDataExistExceptions("Metadata for resource ID=%d already exists".formatted(entityDto.id));
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

        EntitySongDTO entitySongDTO = EntitySongDTO.fromEntitySong(entitySong);

        return entitySongDTO;
    }

    public List<EntitySongDTO> getListSong() {
        return repositorySong.findAll().stream().map(EntitySongDTO::fromEntitySong).toList();
    }

    public List<Long> deleteAllSongById(String data) {
        List<Long> Ids = Arrays.stream(data.split(",")).map(Long::valueOf).toList();
        List<Long> existingIds = repositorySong.findExistingIds(Ids);
        repositorySong.deleteAllByIdInBatch(existingIds); 

        return existingIds;
    }
}