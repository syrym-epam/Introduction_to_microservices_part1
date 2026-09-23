package com.microservice.song_service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.song_service.dto.EntitySongDTO;
import com.microservice.song_service.dto.ResultIdDTO;
import com.microservice.song_service.dto.ResultIdsDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Validated 
@RestController 
@RequestMapping("/songs")
public class ControllerSong {

    private ServiceSong serviceSong;

    public ControllerSong(ServiceSong serviceSong) {
        this.serviceSong = serviceSong;
    }
    
    @PostMapping()
    public ResponseEntity<ResultIdDTO> createSong(@Valid @RequestBody EntitySongDTO entitySongDto) {

        Long id = serviceSong.saveSong(entitySongDto);
        
        return ResponseEntity.status(HttpStatus.OK).body(new ResultIdDTO(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntitySongDTO> getSongById(
            @Positive(message = "Invalid value '%s' for ID. Must be a positive integer") @PathVariable Long id) {
        EntitySongDTO entitySongDTO = serviceSong.getSongById(id);

        return ResponseEntity.status(HttpStatus.OK).body(entitySongDTO);
    }

    @DeleteMapping()
    public ResponseEntity<ResultIdsDTO> deleteAllSongById(
            @RequestParam("id") 
            @Size(max = 200, message = "CSV string is too long: received %s characters, maximum allowed is 200")
            @Pattern(
                regexp = "^[1-9]\\d*(,[1-9]\\d*)*$", 
                message = "Invalid ID format: '%s'. Only positive integers are allowed"
            )
            String Ids) {

        List<Long> existingIds = serviceSong.deleteAllSongById(Ids);

        return ResponseEntity.status(HttpStatus.OK).body(new ResultIdsDTO(existingIds));
    }
}
