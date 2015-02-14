package com.smartbear.collab.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by mzumbado on 2/12/15.
 */
public class JsonrpcResponse implements Serializable {

    private List<JsonrpcCommandResponse> results;

    public List<JsonrpcCommandResponse> getResults() {
        return results;
    }

    public void setResults(List<JsonrpcCommandResponse> results) {
        this.results = results;
    }
}
