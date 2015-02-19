package com.smartbear.collab.common.model.impl;

import com.smartbear.collab.common.model.JsonrpcCommand;

/**
 * Created by mzumbado on 2/19/15.
 */
public class GetActionItems extends JsonrpcCommand{
    private static final String COMMAND_NAME = "UserService.getActionItems";

    public GetActionItems() {
        this.setCommand(COMMAND_NAME);
    }

}
