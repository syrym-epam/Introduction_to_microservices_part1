package com.microservice.song_service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository 
public interface RepositorySong extends JpaRepository<EntitySong, Long> {

    @Query("SELECT i.id FROM EntitySong i WHERE i.id IN :ids")
    List<Long> findExistingIds(@Param("ids") List<Long> Ids);
}
