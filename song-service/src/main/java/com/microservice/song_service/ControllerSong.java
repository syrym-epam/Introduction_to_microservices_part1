package com.microservice.song_service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.song_service.dto.EntitySongDTO;
import com.microservice.song_service.dto.ResultIdDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController 
@RequestMapping("/songs")
public class ControllerSong {

    private ServiceSong serviceSong;

    ControllerSong(ServiceSong serviceSong) {
        this.serviceSong = serviceSong;
    }
    
    @PostMapping()
    public ResponseEntity<ResultIdDTO> createSong(@Valid @RequestBody EntitySongDTO entitySongDto) {

        Long id = serviceSong.saveSong(entitySongDto);
        
        return ResponseEntity.status(HttpStatus.OK).body(new ResultIdDTO(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntitySongDTO> getSongById(@PathVariable Long id) {
        EntitySongDTO entitySongDTO = serviceSong.getSongById(id);

        return ResponseEntity.status(HttpStatus.OK).body(entitySongDTO);
    }

    @GetMapping()
    public ResponseEntity<List<EntitySongDTO>> getListSong() {
        return ResponseEntity.status(HttpStatus.OK).body(serviceSong.getListSong());
    }
    
    
    @DeleteMapping()
    public ResponseEntity<List<Long>> deleteAllSongById(@Size(max = 200, message = "CSV string length must not exceed 200 characters") @RequestParam("id") List<Long> ids) {
        serviceSong.deleteAllSongById(ids);
        return ResponseEntity.status(HttpStatus.OK).body(ids);
    }
}
