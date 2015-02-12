package com.smartbear.collab.client;

import com.smartbear.collab.common.model.JsonrpcCommandResponse;
import com.smartbear.collab.common.model.JsonrpcResponse;
import com.smartbear.collab.common.model.JsonrpcResult;
import com.smartbear.collab.common.model.impl.GetLoginTicket;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by mzumbado on 2/4/15.
 */
public class JerseyTest {

    public static void main(String[] args) {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080").path("services/json/v1");

        GetLoginTicket jsonrpcMethod = new GetLoginTicket("mzumbado", "");

        List<GetLoginTicket> methods = new ArrayList<GetLoginTicket>();
        methods.add(jsonrpcMethod);

//        List<LinkedHashMap<String, LinkedHashMap<String, String>> > results = new ArrayList<LinkedHashMap<String, LinkedHashMap<String, String>> >();
        List<JsonrpcCommandResponse> results;

        results = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(methods, MediaType.APPLICATION_JSON_TYPE),
                        List.class);

        System.out.println("response = " + results.get(0).getResult().toString());
//        System.out.println(results.get(0).get("result").toString());
        LinkedHashMap<String, LinkedHashMap<String, String>> firstResponse = results.get(0).getResult();
        System.out.println("result = " + firstResponse.get("result"));
        System.out.println("loginTicket = " + firstResponse.get("result").get("loginTicket"));

    }

}
