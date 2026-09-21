package com.microservice.song_service.dto;


import java.util.Map; 


public record ErrorValidationResponseDTO(
    String errorCode,
    String errorMessage,
    Map<String, String> details
) {}
