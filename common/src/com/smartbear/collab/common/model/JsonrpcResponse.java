package com.smartbear.collab.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by mzumbado on 2/12/15.
 */
public class JsonrpcResponse implements Serializable {

    @JsonProperty("result")
    private List<LinkedHashMap<String, LinkedHashMap<String, String>> > result;

    @JsonProperty("errors")
    private List<String> errors;

    public List<LinkedHashMap<String, LinkedHashMap<String, String>>> getResult() {
        return result;
    }

    public void setResult(List<LinkedHashMap<String, LinkedHashMap<String, String>>> result) {
        this.result = result;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
