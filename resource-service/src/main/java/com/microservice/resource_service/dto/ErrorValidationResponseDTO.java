package com.microservice.resource_service.dto;

import java.time.LocalDateTime;
import java.util.Map; 

public record ErrorValidationResponseDTO(
    int errorCode,
    String errorMessage,
    LocalDateTime timestamp,
    Map<String, String> details
) {}
