package com.smartbear.collab.common.model.impl;

import com.smartbear.collab.common.model.JsonrpcCommand;

/**
 * Created by mzumbado on 2/19/15.
 */
public class Authenticate extends JsonrpcCommand {
    static final String COMMAND_NAME = "SessionService.authenticate";

    public Authenticate(String username, String ticketId) {
        this.setCommand(COMMAND_NAME);
        this.setArgs(new AuthenticateArgs(username, ticketId));
    }

}
