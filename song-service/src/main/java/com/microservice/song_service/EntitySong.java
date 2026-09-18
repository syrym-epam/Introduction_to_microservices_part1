package com.microservice.song_service;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity 
@Data 
public class EntitySong {

    @Id 
    Long id;

    @Column(length = 100)
    String name;

    @Column(length = 100)
    String artist;

    @Column(length = 100)
    String album;

    @Column(length = 5)
    String duration;

    @Column(length = 4)
    String year;
}