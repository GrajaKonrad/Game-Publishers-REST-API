package com.Exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.NOT_FOUND, reason = "Entity not found")
public class EntityNotFound extends RuntimeException{
    public EntityNotFound(String message){
        super(message);
    }
}
