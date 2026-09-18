package com.microservice.resource_service.dto;


public record EntitySongDTO(
        Long id,
        String name,
        String artist,
        String album,
        String duration,
        String year
) { }
