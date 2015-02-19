package com.smartbear.collab.client;

import com.smartbear.collab.client.exception.ClientException;
import com.smartbear.collab.client.exception.CredentialsException;
import com.smartbear.collab.client.exception.ServerURLException;
import com.smartbear.collab.common.model.*;
import com.smartbear.collab.common.model.impl.Authenticate;
import com.smartbear.collab.common.model.impl.Credentials;
import com.smartbear.collab.common.model.impl.GetActionItems;
import com.smartbear.collab.common.model.impl.GetLoginTicket;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    public JsonrpcResponse sendRequest(List<JsonrpcCommand> methods){
        JsonrpcResponse result = new JsonrpcResponse();

        List<LinkedHashMap<String, LinkedHashMap<String, String>> > results = new ArrayList<LinkedHashMap<String, LinkedHashMap<String, String>> >();

        results = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(methods, MediaType.APPLICATION_JSON_TYPE),
                        List.class);

        List<JsonrpcCommandResponse> commandResponses = new ArrayList<JsonrpcCommandResponse>();
        for (LinkedHashMap commandResultMap : results){
            JsonrpcCommandResponse commandResponse = new JsonrpcCommandResponse();
            if (commandResultMap.get("result") != null){
                JsonrpcResult commandResult = new JsonrpcResult();
                LinkedHashMap<String, String> commandResultValue = (LinkedHashMap<String,String>)commandResultMap.get("result");
                if (commandResultValue.size() > 0) {
                    commandResult.setCommand(commandResultValue.keySet().iterator().next());
                    commandResult.setValue(commandResultValue.get(commandResult.getCommand()));
                    commandResponse.setResult(commandResult);
                }
            }
            if (commandResultMap.get("errors") != null){
                List<LinkedHashMap<String,String>> errors = (List<LinkedHashMap<String,String>>)commandResultMap.get("errors");
                List<JsonrpcError> jsonrpcErrors = new ArrayList<JsonrpcError>();
                for (LinkedHashMap<String,String> error : errors){
                    JsonrpcError jsonrpcError = new JsonrpcError();
                    jsonrpcError.setCode(error.get("code"));
                    jsonrpcError.setMessage(error.get("message"));
                    jsonrpcErrors.add(jsonrpcError);
                }
                commandResponse.setErrors(jsonrpcErrors);

            }
            commandResponses.add(commandResponse);
        }
        result.setResults(commandResponses);

        return result;
    }

    public JsonrpcCommandResponse login(String username, String password) throws ServerURLException, CredentialsException, Exception{

        JsonrpcCommandResponse result = new JsonrpcCommandResponse();

        List<JsonrpcCommand> methods = new ArrayList<JsonrpcCommand>();
        methods.add(new GetLoginTicket(username, password));
        try {

            JsonrpcResponse response = sendRequest(methods);
            result = response.getResults().get(0);

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
        return result;
    }

    public JsonrpcCommandResponse getActionItems(String username, String ticketId) throws ServerURLException, CredentialsException, Exception{

        JsonrpcCommandResponse result = new JsonrpcCommandResponse();

        List<JsonrpcCommand> methods = new ArrayList<JsonrpcCommand>();
        methods.add(new Authenticate(username, ticketId));
        methods.add(new GetActionItems());
        try {

            JsonrpcResponse response = sendRequest(methods);
            result = response.getResults().get(0);
            if (result.getErrors() != null && result.getErrors().size() > 0){
                throw new ServerURLException("Server communication error.");
            }
            result = response.getResults().get(1);

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
        return result;
    }
}
