package com.smartbear.collab.common.model.impl;

import com.smartbear.collab.common.model.JsonrpcArgs;

import java.util.List;

/**
 * Created by mzumbado on 2/26/15.
 */
public class AddFilesArgs implements JsonrpcArgs {
    private String reviewId;
    private List<ChangeList> changelists;

    public AddFilesArgs(String reviewId, List<ChangeList> changelists) {
        this.reviewId = reviewId;
        this.changelists = changelists;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public List<ChangeList> getChangelists() {
        return changelists;
    }

    public void setChangeLists(List<ChangeList> changelists) {
        this.changelists = changelists;
    }
}
