package com.smartbear.collab.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;

/**
 * Created by mzumbado on 2/12/15.
 */
@JsonRootName("error")
public class JsonrpcError implements Serializable{

    @JsonProperty("code")
    private String code;
    @JsonProperty("message")
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
