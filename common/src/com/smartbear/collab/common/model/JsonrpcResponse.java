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
