package com.smartbear.collab.client.exception;

/**
 * Created by miguelon on 2/11/15.
 */
public abstract class ClientException extends Exception{
    public ClientException(String message){
        super(message);
    }
}
