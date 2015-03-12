package com.smartbear.collab.common.model.impl;

import java.util.List;

/**
 * Created by mzumbado on 2/26/15.
 */
public class ChangeList {
    private ScmToken scmToken;
    private List<String> scmConnectionParameters;
    private String zipName;
    private CommitInfo commitInfo;
    private List<Version> versions;

    public ChangeList(ScmToken scmToken, List<String> scmConnectionParameters, String zipName, CommitInfo commitInfo, List<Version> versions) {
        this.scmToken = scmToken;
        this.scmConnectionParameters = scmConnectionParameters;
        this.zipName = zipName;
        this.commitInfo = commitInfo;
        this.versions = versions;
    }

    public ScmToken getScmToken() {
        return scmToken;
    }

    public void setScmToken(ScmToken scmToken) {
        this.scmToken = scmToken;
    }

    public List<String> getScmConnectionParameters() {
        return scmConnectionParameters;
    }

    public void setScmConnectionParameters(List<String> scmConnectionParameters) {
        this.scmConnectionParameters = scmConnectionParameters;
    }

    public String getZipName() {
        return zipName;
    }

    public void setZipName(String zipName) {
        this.zipName = zipName;
    }

    public CommitInfo getCommitInfo() {
        return commitInfo;
    }

    public void setCommitInfo(CommitInfo commitInfo) {
        this.commitInfo = commitInfo;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }
}
