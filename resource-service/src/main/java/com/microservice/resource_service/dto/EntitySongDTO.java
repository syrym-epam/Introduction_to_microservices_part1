package com.microservice.resource_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  
public class EntitySongDTO {
        Long id;
        String name;
        String artist;
        String album;
        String duration;
        String year;
}
