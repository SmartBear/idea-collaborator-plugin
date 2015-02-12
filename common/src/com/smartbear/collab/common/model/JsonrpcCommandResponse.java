package com.smartbear.collab.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by mzumbado on 2/12/15.
 */

public class JsonrpcCommandResponse implements Serializable {

    @JsonProperty("result")
    private LinkedHashMap<String, LinkedHashMap<String, String>> result;

    public LinkedHashMap<String, LinkedHashMap<String, String>> getResult() {
        return result;
    }

    public void setResult(LinkedHashMap<String, LinkedHashMap<String, String>> result) {
        this.result = result;
    }
}
