package com.smartbear.collab.common.model.impl;

import com.smartbear.collab.common.model.JsonrpcCommand;

/**
 * Created by mzumbado on 2/26/15.
 */
public class AddFiles extends JsonrpcCommand {
    private static final String COMMAND_NAME = "ReviewService.addFiles";

    public AddFiles() {
        this.setCommand(COMMAND_NAME);
    }
}
