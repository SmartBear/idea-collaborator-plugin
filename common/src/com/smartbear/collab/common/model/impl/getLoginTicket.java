package com.smartbear.collab.common.model.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartbear.collab.common.model.JsonrpcCommand;

/**
 * Created by mzumbado on 2/5/15.
 */
public class getLoginTicket extends JsonrpcCommand {
    static final String COMMAND_NAME = "SessionService.getLoginTicket";

    public getLoginTicket(String login, String password) {
        this.setCommand(COMMAND_NAME);
        this.setArgs(new Credentials(login, password));
    }
}
