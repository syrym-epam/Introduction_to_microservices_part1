package com.microservice.song_service.dto;


import com.microservice.song_service.EntitySong;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class EntitySongDTO {

    public static EntitySongDTO fromEntitySong(EntitySong entitySong) {
        if(entitySong == null)
            return null;
        EntitySongDTO entitySongDTO = new EntitySongDTO();
        entitySongDTO.setId(entitySong.getId());
        entitySongDTO.setAlbum(entitySong.getAlbum());
        entitySongDTO.setArtist(entitySong.getArtist());
        entitySongDTO.setName(entitySong.getName());
        entitySongDTO.setDuration(entitySong.getDuration());
        entitySongDTO.setYear(entitySong.getYear());
        
        return entitySongDTO;
    }

    @NotNull(message = "ID is required") 
    @Positive(message = "ID must be a positive number")
    public Long id;

    @NotNull(message = "Song name is required")
    @Size(min = 1, max = 100, message="Song name must be between 1 and 100 characters")
    public String name;

    @NotNull(message = "Artist name is required")
    @Size(min = 1, max = 100, message="Artist name must be between 1 and 100 characters")
    public String artist;

    @NotNull(message = "Album name is required")
    @Size(min = 1, max = 100, message="Album name must be between 1 and 100 characters")
    public String album;

    @NotNull(message = "Duration is required")
    @Pattern(regexp = "^[0-5]\\d:[0-5]\\d$", message = "Duration must be in mm:ss format with leading zeros")
    public String duration;

    @NotNull(message = "Year is required")
    @Pattern(regexp = "^(19|20)\\d{2}$", message = "Year must be between 1900 and 2099")
    public String year;
}
