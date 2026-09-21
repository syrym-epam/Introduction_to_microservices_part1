package com.microservice.resource_service;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.resource_service.dto.ResourceIdDTO;
import com.microservice.resource_service.dto.ResourceIdsDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


@Validated 
@RequestMapping("/resources")
@RestController
public class ControllerResource {

    private final static String MEDIA_TYPE_MP3 = "audio/mpeg";

    private ServiceResource serviceResource;

    public ControllerResource(ServiceResource serviceResource) {
        this.serviceResource = serviceResource;
    }

    @PostMapping()
    public ResponseEntity<ResourceIdDTO> creatResource(@RequestBody byte[] data, HttpServletRequest request) throws IOException {

        Long id = serviceResource.preSaveResource(data, request);

        return ResponseEntity.status(HttpStatus.OK).body(new ResourceIdDTO(id));
    }

    @GetMapping("/exist/{id}")
    public ResponseEntity<Boolean> checkResourceForExist(@PathVariable Long id) {
        return ResponseEntity.status(200).body(serviceResource.existResource(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getResource( 
            @Positive(message = "Invalid value '%s' for ID. Must be a positive integer")
            @PathVariable 
            Long id) {
        EntityResource entityResource = serviceResource.getResource(id);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(MEDIA_TYPE_MP3))
                .body(entityResource.getFileData());
    }
    
    @DeleteMapping()
    public ResponseEntity<ResourceIdsDTO> deleteResource(
            @RequestParam("id") 
            @Size(max = 200, message = "CSV string is too long: received %s characters, maximum allowed is 200")
            @Pattern(
                regexp = "^[1-9]\\d*(,[1-9]\\d*)*$", 
                message = "Invalid ID format: '%s'. Only positive integers are allowed"
            )
            String Ids) {

        List<Long> existingIds = serviceResource.deleteAllResourceByIDs(Ids);

        return ResponseEntity.status(HttpStatus.OK).body(new ResourceIdsDTO(existingIds));
    }

    @DeleteMapping("/{Id}")
    public ResponseEntity<ResourceIdDTO> deleteResource(@PathVariable Long Id) {

        serviceResource.deleteResourceFileById(Id);

        return ResponseEntity.status(HttpStatus.OK).body(new ResourceIdDTO(Id));
    }
}
