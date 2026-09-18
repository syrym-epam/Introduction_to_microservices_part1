package com.microservice.resource_service.exceptions;


public class DataNotFoundException extends RuntimeException{

    public DataNotFoundException(String s) {
        super(s);
    }
    
}
