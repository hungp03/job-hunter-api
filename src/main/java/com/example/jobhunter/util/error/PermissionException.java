package com.example.jobhunter.util.error;

public class PermissionException extends RuntimeException{
    public PermissionException(String mess){
        super(mess);
    }
}
