package com.microservice.resource_service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryResource extends JpaRepository<EntityResource, Long> {


    @Query("SELECT i.id FROM EntityResource i WHERE i.id IN :ids")
    List<Long> findExistingIds (@Param("ids") List<Long> Ids);
}


