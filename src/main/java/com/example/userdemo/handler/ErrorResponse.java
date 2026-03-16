package com.example.userdemo.handler;

import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
public class ErrorResponse {

    private String timestamp;
    private int status;
    private String error;
    private String message;

    public ErrorResponse(
        int status,
        String error,
        String message
    ) {
        timestamp =
            (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"))
                    .format(new Date());
        this.status = status;
        this.error = error;
        this.message = message;
    }

}