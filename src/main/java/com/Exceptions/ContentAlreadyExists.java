package com.Exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(code = org.springframework.http.HttpStatus.CONFLICT, reason = "Content already exists")
public class ContentAlreadyExists extends RuntimeException {
    public ContentAlreadyExists(String message){
        super(message);
    }
    
}
