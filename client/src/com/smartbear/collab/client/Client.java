package com.smartbear.collab.client;

import com.smartbear.collab.client.exception.ClientException;
import com.smartbear.collab.client.exception.CredentialsException;
import com.smartbear.collab.client.exception.ServerURLException;
import com.smartbear.collab.common.model.JsonrpcCommand;
import com.smartbear.collab.common.model.JsonrpcResult;
import com.smartbear.collab.common.model.impl.Credentials;
import com.smartbear.collab.common.model.impl.GetLoginTicket;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mzumbado on 2/10/15.
 */
public class Client {
    static WebTarget target;

    public Client(String URLStr) {
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

    public List login(String username, String password) throws ServerURLException, CredentialsException, Exception{

        List<JsonrpcResult> results = new ArrayList<JsonrpcResult>();

        List<JsonrpcCommand> methods = new ArrayList<JsonrpcCommand>();
        methods.add(new GetLoginTicket(username, password));
        try {
            results = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(methods, MediaType.APPLICATION_JSON_TYPE),
                            List.class);
        }
        catch (IllegalArgumentException iae) {
            throw new ServerURLException(iae.getMessage());
        }
        catch (ProcessingException pe) {
            if (pe.getCause().getClass().equals(ConnectException.class)){
                throw new ServerURLException(pe.getCause().getMessage());
            }
            else {
                throw new ServerURLException(pe.getMessage());
            }
        }
        catch (NotFoundException nfe) {
            throw new ServerURLException(nfe.getMessage());
        }
        catch (Exception e){
            throw new CredentialsException(e.getMessage());
        }
        return results;
    }
}
