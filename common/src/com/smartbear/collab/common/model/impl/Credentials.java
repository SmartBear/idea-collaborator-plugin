package com.smartbear.collab.common.model.impl;

import com.smartbear.collab.common.model.JsonrpcArgs;

import java.io.Serializable;

/**
 * Created by mzumbado on 2/5/15.
 */
public class Credentials implements JsonrpcArgs {
    private String login;
    private String password;

    public Credentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    // though unused in plugin code, these methods are leveraged by Jackson to make JSON serializations:

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
