package com.smartbear.collab.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.smartbear.collab.client.exception.ClientException;
import com.smartbear.collab.client.exception.CredentialsException;
import com.smartbear.collab.client.exception.ServerURLException;
import com.smartbear.collab.common.model.*;
import com.smartbear.collab.common.model.impl.*;
import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.media.multipart.*;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by mzumbado on 2/10/15.
 */
public class Client {
    static WebTarget target;
    private String serverURL;
    private String username;
    private String ticketId;

    Logger logger = Logger.getLogger(Client.class.toString());

    public Client(String URLStr) {
        javax.ws.rs.client.Client client = ClientBuilder.newClient();
        this.serverURL = URLStr;
        this.target = client.target(URLStr).path("services/json/v1");
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public boolean hasCredentials(){
        return ((username != null && !username.isEmpty() && (ticketId != null && !ticketId.isEmpty())));

    }

    public JsonrpcResponse sendRequest(List<JsonrpcCommand> methods){
        JsonrpcResponse result = new JsonrpcResponse();

        List<LinkedHashMap<String, LinkedHashMap<String, String>> > results = new ArrayList<LinkedHashMap<String, LinkedHashMap<String, String>> >();

        ObjectMapper mapper = new ObjectMapper();
        try {
            logger.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(methods));
        }
        catch (JsonProcessingException jpe){
        }

        results = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(methods, MediaType.APPLICATION_JSON_TYPE),
                        List.class);

        try {
            logger.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(results));
        }
        catch (JsonProcessingException jpe){
        }

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

    public boolean sendZip(Map.Entry<String, byte[]> zipFile) {
        javax.ws.rs.client.Client zipClient = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
        WebTarget webTarget = zipClient.target(serverURL).path("contentupload");

        final FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        final FormDataContentDisposition disposition = FormDataContentDisposition
                .name("file")
                .fileName(zipFile.getKey())
                .size(zipFile.getValue().length)
                .build();
        final FormDataBodyPart bodyPart = new FormDataBodyPart(disposition, zipFile.getValue(), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        formDataMultiPart.bodyPart(bodyPart);

/*
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

        File file = new File(zipFile.getKey());
        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(zipFile.getValue());
            fis.close();
        }
        catch (Exception e){}

        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart(zipFile.getKey(), file,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        multiPart.bodyPart(fileDataBodyPart);
*/
        Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", "BASIC " + Base64.encodeAsString(username + ":"))
        .header("WWW-authenticate-CodeCollabTicket", ticketId)
                .post(Entity.entity(formDataMultiPart, formDataMultiPart.getMediaType()));

        logger.info("Authorization : BASIC " + Base64.encodeAsString(username + ":"));
        logger.info("WWW-authenticate-CodeCollabTicket :" + ticketId);

        if (response.getStatus() == 200){
            return true;
        }
        return false;
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
        this.username = username;
        this.ticketId = (String)result.getResult().getValue();
        return result;
    }

    public JsonrpcCommandResponse getActionItems() throws ServerURLException, CredentialsException, Exception{

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

    public JsonrpcCommandResponse checkTicket() throws ServerURLException, CredentialsException, Exception{

        JsonrpcCommandResponse result = new JsonrpcCommandResponse();

        List<JsonrpcCommand> methods = new ArrayList<JsonrpcCommand>();
        methods.add(new Authenticate(username, ticketId));
        try {

            JsonrpcResponse response = sendRequest(methods);
            result = response.getResults().get(0);
            if (result.getErrors() != null && result.getErrors().size() > 0){
                throw new ServerURLException(result.getErrors().get(0).getMessage());
            }
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

    public JsonrpcCommandResponse createReview(String creator, String title) throws ServerURLException, CredentialsException, Exception{

        JsonrpcCommandResponse result = new JsonrpcCommandResponse();

        List<JsonrpcCommand> methods = new ArrayList<JsonrpcCommand>();
        methods.add(new Authenticate(username, ticketId));
        methods.add(new CreateReview(creator, title));
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

    public JsonrpcCommandResponse addFilesToReview(String reviewId, List<ChangeList> changeLists) throws ServerURLException, CredentialsException, Exception{

        JsonrpcCommandResponse result = new JsonrpcCommandResponse();

        List<JsonrpcCommand> methods = new ArrayList<JsonrpcCommand>();
        methods.add(new Authenticate(username, ticketId));
        methods.add(new AddFiles(reviewId, changeLists));
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
