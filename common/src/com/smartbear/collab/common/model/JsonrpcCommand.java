package com.smartbear.collab.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by miguelon on 2/9/15.
 */
public abstract class JsonrpcCommand implements Serializable {
    @JsonProperty("command")
    private String command;
    @JsonProperty("args")
    private JsonrpcArgs args;

    // though unused in plugin code, the get* methods are leveraged by Jackson to make JSON serializations:

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public JsonrpcArgs getArgs() {
        return args;
    }

    public void setArgs(JsonrpcArgs args) {
        this.args = args;
    }

}
