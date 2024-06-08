package com.Exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.BAD_REQUEST, reason = "Recived partial data")
public class RecivedPartialData extends RuntimeException{
    public RecivedPartialData(String message){
        super(message);
    }
}
