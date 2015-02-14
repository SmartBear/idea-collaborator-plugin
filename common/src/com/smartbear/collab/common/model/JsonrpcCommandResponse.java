package com.smartbear.collab.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by mzumbado on 2/12/15.
 */

public class JsonrpcCommandResponse {

    @JsonProperty("result")
    private JsonrpcResult result;

    @JsonProperty("errors")
    private List<JsonrpcError> errors;

    public JsonrpcResult getResult() {
        return result;
    }

    public void setResult(JsonrpcResult result) {
        this.result = result;
    }

    public List<JsonrpcError> getErrors() {
        return errors;
    }

    public void setErrors(List<JsonrpcError> errors) {
        this.errors = errors;
    }
}
