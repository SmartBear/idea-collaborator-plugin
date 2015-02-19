package com.smartbear.collab.common.model.impl;

import com.smartbear.collab.common.model.JsonrpcArgs;

/**
 * Created by mzumbado on 2/19/15.
 */
public class AuthenticateArgs implements JsonrpcArgs {
    private String login;
    private String ticket;

    public AuthenticateArgs(String login, String ticket) {
        this.login = login;
        this.ticket = ticket;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
