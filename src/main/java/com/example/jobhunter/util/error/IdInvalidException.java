package com.example.jobhunter.util.error;

public class IdInvalidException extends RuntimeException{
    public IdInvalidException(String mess){
        super(mess);
    }
}
