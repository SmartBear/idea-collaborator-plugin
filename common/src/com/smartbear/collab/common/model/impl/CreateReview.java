package com.smartbear.collab.common.model.impl;

import com.smartbear.collab.common.model.JsonrpcCommand;

/**
 * Created by miguelon on 2/25/15.
 */
public class CreateReview extends JsonrpcCommand {
/*
    {"command" : "ReviewService.createReview",
               "args" :{
   "creator" : "jsmith",
   "title" : "Check JDK version",
   "deadline" : "2015-02-01T09:00:00Z",
   "accessPolicy" : "PARTICIPANTS"
 }
     */
    private static final String COMMAND_NAME = "ReviewService.createReview";

    public CreateReview(String creator, String title) {
        this.setCommand(COMMAND_NAME);
        this.setArgs(new CreateReviewArgs(creator, title));
    }

}
