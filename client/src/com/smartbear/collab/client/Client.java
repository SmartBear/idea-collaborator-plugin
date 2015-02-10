package com.smartbear.collab.client;

import com.smartbear.collab.common.model.JsonrpcCommand;
import com.smartbear.collab.common.model.JsonrpcResult;
import com.smartbear.collab.common.model.impl.Credentials;
import com.smartbear.collab.common.model.impl.GetLoginTicket;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mzumbado on 2/10/15.
 */
public class Client {
    static WebTarget target;

    public Client(String URLStr){
        javax.ws.rs.client.Client client = ClientBuilder.newClient();
        this.target = client.target(URLStr).path("services/json/v1");
    }

    public List sendRequest(List<JsonrpcCommand> methods){
        List<JsonrpcResult> results = new ArrayList<JsonrpcResult>();

        results = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(methods, MediaType.APPLICATION_JSON_TYPE),
                        List.class);
        return results;
    }

    public List login(String username, String password){

        List<JsonrpcResult> results = new ArrayList<JsonrpcResult>();

        List<JsonrpcCommand> methods = new ArrayList<JsonrpcCommand>();
        methods.add(new GetLoginTicket(username, password));
        results = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(methods, MediaType.APPLICATION_JSON_TYPE),
                        List.class);
        return results;
    }
}
