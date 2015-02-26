package com.smartbear.collab.common.model.impl;

/**
 * Created by mzumbado on 2/26/15.
 */
public class BaseVersion {
    private String action;
    private String md5;
    private CommitInfo commitInfo;
    private String source;
    private String scmVersionName;
    private String scmPath;

    public BaseVersion(String action, String md5, CommitInfo commitInfo, String source, String scmVersionName, String scmPath) {
        this.action = action;
        this.md5 = md5;
        this.commitInfo = commitInfo;
        this.source = source;
        this.scmVersionName = scmVersionName;
        this.scmPath = scmPath;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public CommitInfo getCommitInfo() {
        return commitInfo;
    }

    public void setCommitInfo(CommitInfo commitInfo) {
        this.commitInfo = commitInfo;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getScmVersionName() {
        return scmVersionName;
    }

    public void setScmVersionName(String scmVersionName) {
        this.scmVersionName = scmVersionName;
    }

    public String getScmPath() {
        return scmPath;
    }

    public void setScmPath(String scmPath) {
        this.scmPath = scmPath;
    }
}
