package com.smartbear.collab.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbear.collab.common.model.JsonrpcCommandResponse;
import com.smartbear.collab.common.model.JsonrpcResponse;
import com.smartbear.collab.common.model.JsonrpcResult;
import com.smartbear.collab.common.model.impl.GetLoginTicket;

//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by mzumbado on 2/4/15.
 */
public class JerseyTest {

    public static void main(String[] args) {


        Client client = new Client("http://localhost:8080");
        JsonrpcCommandResponse response = null;
        try {
            response = client.login("mzumbadov", "");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        if (response.getResult() != null) {
            System.out.println("command " + response.getResult().getCommand() + " value " + response.getResult().getValue());
        }

        if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            System.out.println("error " + response.getErrors().get(0).getCode() + " value " + response.getErrors().get(0).getMessage());
        }

/*        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080").path("services/json/v1");

        GetLoginTicket jsonrpcMethod = new GetLoginTicket("mzumbado", "");

        List<GetLoginTicket> methods = new ArrayList<GetLoginTicket>();
        methods.add(jsonrpcMethod);

        client.
//        List<LinkedHashMap<String, LinkedHashMap<String, String>> > results = new ArrayList<LinkedHashMap<String, LinkedHashMap<String, String>> >();
        String results;
*/

/*
        results = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(methods, MediaType.APPLICATION_JSON_TYPE),
                        String.class);

        ObjectMapper mapper = new ObjectMapper();
        List<LinkedHashMap<String, LinkedHashMap<String, String>> > results2;
        try {

            //convert JSON string to Map
            results2 = mapper.readValue(results,
                    new TypeReference<List<LinkedHashMap<String, LinkedHashMap<String, String>> >>(){});

            System.out.println(results2);
            System.out.println("result = " + results2.get(0));
//            System.out.println("logintqt = " + results2.get(0).get;

        } catch (Exception e) {
            e.printStackTrace();
        }
*/
//        System.out.println("response = " + results.get(0).getResult());
//        System.out.println(results.get(0).get("result").toString());
//        LinkedHashMap<String, LinkedHashMap<String, String>> firstResponse = results.get(0).getResult();
//        System.out.println("result = " + firstResponse.get("result"));
//        System.out.println("loginTicket = " + firstResponse.get("result").get("loginTicket"));

    }

}
