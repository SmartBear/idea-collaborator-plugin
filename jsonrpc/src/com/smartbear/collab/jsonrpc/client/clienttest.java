package com.smartbear.collab.jsonrpc.client;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created by mzumbado on 2/3/15.
 */
public class clienttest {

    public static void main(String[] args){
        // The JSON-RPC 2.0 server URL
        URL serverURL = null;

        try {
            serverURL = new URL("http://localhost:8080/services/json/v1");

        } catch (MalformedURLException e) {
            // handle exception...
        }

// Create new JSON-RPC 2.0 client session
        JSONRPC2Session mySession = new JSONRPC2Session(serverURL);

// Construct new request
        String method = "command";
        int requestID = 0;
        JSONRPC2Request request = new JSONRPC2Request(method, requestID);

        // Send request
        JSONRPC2Response response = null;

        try {
            response = mySession.send(request);

        } catch (JSONRPC2SessionException e) {

            System.err.println(e.getMessage());
            // handle exception...
        }

        // Print response result / error
        if (response.indicatesSuccess())
            System.out.println(response.getResult());
        else
            System.out.println(response.getError().getMessage());
    }
}
