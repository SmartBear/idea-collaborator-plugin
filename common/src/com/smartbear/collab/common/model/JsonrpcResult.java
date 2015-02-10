package com.smartbear.collab.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;

/**
 * Created by mzumbado on 2/5/15.
 */
@JsonRootName("result")
public class JsonrpcResult implements Serializable{
    @JsonProperty("name")
    private String result;
    @JsonProperty("errors")
    private String errors;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

}
