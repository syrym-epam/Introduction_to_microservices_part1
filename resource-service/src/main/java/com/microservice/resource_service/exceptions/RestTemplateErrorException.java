package com.microservice.resource_service.exceptions;

public class RestTemplateErrorException extends RuntimeException {

    public RestTemplateErrorException(String msg) {
        super(msg);
    }
}
