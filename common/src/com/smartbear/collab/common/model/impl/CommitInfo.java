package com.smartbear.collab.common.model.impl;

import java.util.Date;

/**
 * Created by mzumbado on 2/26/15.
 */
public class CommitInfo {
    private String comment;
    private Date date;
    private String author;
    private boolean local;
    private String scmId;
    private String hostGuid;

    public CommitInfo(String comment, Date date, String author, boolean local, String scmId, String hostGuid) {
        this.comment = comment;
        this.date = date;
        this.author = author;
        this.local = local;
        this.scmId = scmId;
        this.hostGuid = hostGuid;
    }

    // though unused in plugin code, these methods are leveraged by Jackson to make JSON serializations:

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public String getScmId() {
        return scmId;
    }

    public void setScmId(String scmId) {
        this.scmId = scmId;
    }

    public String getHostGuid() {
        return hostGuid;
    }

    public void setHostGuid(String hostGuid) {
        this.hostGuid = hostGuid;
    }
}
