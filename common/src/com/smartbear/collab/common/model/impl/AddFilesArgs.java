package com.smartbear.collab.common.model.impl;

import com.smartbear.collab.common.model.JsonrpcArgs;

import java.util.List;

/**
 * Created by mzumbado on 2/26/15.
 */
public class AddFilesArgs implements JsonrpcArgs {
    private String reviewId;
    private List<ChangeList> changeLists;

    public AddFilesArgs(String reviewId, List<ChangeList> changeLists) {
        this.reviewId = reviewId;
        this.changeLists = changeLists;
    }
}
