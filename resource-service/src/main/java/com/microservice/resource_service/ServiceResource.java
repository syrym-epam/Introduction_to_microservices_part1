package com.microservice.resource_service;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.microservice.resource_service.dto.EntitySongDTO;
import com.microservice.resource_service.exceptions.ContentTypeException;
import com.microservice.resource_service.exceptions.DataNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service 
public class ServiceResource {

    @Value("${song-service.url}")
    private String songServiceUrl; 

    private final static String MEDIA_TYPE_MP3 = "audio/mpeg";

    private RepositoryResource repositoryResource;
    private final RestTemplate restTemplate;
    
    public ServiceResource(RepositoryResource repositoryResource) {
        this.repositoryResource = repositoryResource;
        this.restTemplate = new RestTemplate();
    }

    public Long preSaveResource(@RequestBody byte[] data, HttpServletRequest request) throws IOException {
        MimeType mimeType = MimeTypeUtils.parseMimeType(request.getContentType());
        String contentType = "%s/%s".formatted(mimeType.getType(), mimeType.getSubtype());

        if (contentType == null || !contentType.equalsIgnoreCase(MEDIA_TYPE_MP3)) {
            throw new ContentTypeException("Invalid file format: application/json. Only MP3 files are allowed");
        }

        try {
            return saveResource(data);
        } catch (IOException e) {
            throw new IOException("An error occurred on the server");
        } 
    }

    @Transactional 
    public Long saveResource(byte[] data) throws IOException {

        EntityResource entityResource = new EntityResource();
        entityResource.setFileData(data);
        entityResource = repositoryResource.save(entityResource);

        Metadata metadata = extractMetadataFromMP3File(data);

        EntitySongDTO entitySongDTO = new EntitySongDTO(
            entityResource.getId(),
                Optional.ofNullable(metadata.get("title")).orElse(" "),
                Optional.ofNullable(metadata.get("xmpDM:artist"))
                        .or(() -> Optional.ofNullable(metadata.get("author")))
                        .orElse(" "),
                Optional.ofNullable(metadata.get("xmpDM:album")).orElse(" "),
                calcDurationMP3(metadata.get("xmpDM:duration")),
                Optional.ofNullable(
                        metadata.get("xmpDM:releaseDate"))
                        .or(() -> Optional.ofNullable(metadata.get("xmpDM:releaseDate")))
                        .or(() -> Optional.ofNullable(metadata.get("xmpDM:year"))).orElse(" "));

        try {
            restTemplate.postForObject(songServiceUrl + "/songs", entitySongDTO, EntitySongDTO.class);
        } catch(HttpStatusCodeException ex) {

        } catch(ResourceAccessException ex) {

        }

        return entityResource.getId();
    }

    public EntityResource getResource(Long id) {
        if(!existResource(id))
            throw new DataNotFoundException("Resource with ID=%d not found".formatted(id));
        return  repositoryResource
            .findById(id)
            .orElseThrow(() -> new DataNotFoundException("Audio file with %d not found in Database".formatted(id)));
    }

    public Metadata makeMetadataFromClass(Object obj) {
        Metadata metadata = new Metadata();

        Field[] attributes = obj.getClass().getDeclaredFields();
        for(int i = 1; i< attributes.length; i++) {
            try {
                metadata.set(attributes[i].getName(), "" + Optional.ofNullable(attributes[i].get(obj)).orElse(""));
            } catch (IllegalArgumentException e) {
                continue;
            } catch (IllegalAccessException e) {
                continue;
            }
        }

        return metadata;
    }

    public String calcDurationMP3(String value) {
        String duration = "00:00";
        if (value != null) {
            double second = Double.parseDouble(value);
            int totalSecs = (int) Math.round(second);
            int mins = totalSecs / 60;
            int secs = totalSecs % 60;
            duration = "%02d:%02d".formatted(mins, secs);
        }

        return duration;
    }

    public Metadata extractMetadataFromMP3File(byte[] data){
        Metadata metaData = new Metadata();
        try {
            BodyContentHandler handler = new BodyContentHandler();
            ParseContext context = new ParseContext();
            Mp3Parser parser = new Mp3Parser();
            parser.parse(new ByteArrayInputStream(data), handler, metaData, context);
        } catch(Exception e) {
            return metaData;
        }

        return metaData;
    }

    @Transactional
    public List<Long> deleteAllResourceByIDs(String data) {
        List<Long> Ids = Arrays.stream(data.split(",")).map(Long::valueOf).toList();
        List<Long> existingIds = repositoryResource.findExistingIds(Ids);

        repositoryResource.deleteAllByIdInBatch(existingIds);

        String result = existingIds.stream().map(String::valueOf).collect(Collectors.joining(","));

        try {
            restTemplate.delete(songServiceUrl + "/songs?id=" + result);
        } catch (Exception e) {
            System.out.println(e);
        }

        return existingIds;
    }

    public Boolean existResource(Long id) {
        
        return repositoryResource.existsById(id);
    }
}
