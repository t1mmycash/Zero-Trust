package org.example.exceptions;

public class TokenValidationException extends RuntimeException{
    public TokenValidationException(String message){
        super(message);
    }
}
