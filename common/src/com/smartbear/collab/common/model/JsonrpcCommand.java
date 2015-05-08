/*
   Copyright 2015 SmartBear Software, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
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
