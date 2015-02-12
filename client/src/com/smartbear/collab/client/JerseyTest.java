package com.smartbear.collab.client;

import com.smartbear.collab.common.model.JsonrpcResult;
import com.smartbear.collab.common.model.impl.GetLoginTicket;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
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

        List<JsonrpcResult> results = new ArrayList<JsonrpcResult>();

        results = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(methods, MediaType.APPLICATION_JSON_TYPE),
                        List.class);

        System.out.println("name = " + results.get(0) + " value = ");

    }

}
