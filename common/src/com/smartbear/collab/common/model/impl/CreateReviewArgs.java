package com.smartbear.collab.common.model.impl;

import com.smartbear.collab.common.model.JsonrpcArgs;

import java.util.Date;

/**
 * Created by miguelon on 2/25/15.
 */
public class CreateReviewArgs implements JsonrpcArgs {
/*    {"command" : "ReviewService.createReview",
            "args" :{
        "creator" : "jsmith",
                "title" : "Check JDK version",
                "deadline" : "2015-02-01T09:00:00Z",
                "accessPolicy" : "PARTICIPANTS"
    }
    */
    private String creator;
    private String title;
    private Date deadline = null;
    private String accessPolicy = null;

    public CreateReviewArgs(String creator, String title) {
        this.creator = creator;
        this.title = title;
    }

    // though unused in plugin code, these methods are leveraged by Jackson to make JSON serializations:

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDeadline() {
        return deadline;
    }

    public String getAccessPolicy() {
        return accessPolicy;
    }
}
