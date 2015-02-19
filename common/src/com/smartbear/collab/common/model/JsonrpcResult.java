package com.smartbear.collab.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;

/**
 * Created by mzumbado on 2/5/15.
 */
@JsonRootName("result")
public class JsonrpcResult implements Serializable{

    @JsonProperty("command")
    private String command;
    @JsonProperty("value")
    private Object value;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
