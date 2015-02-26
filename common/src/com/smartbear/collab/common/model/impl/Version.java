package com.smartbear.collab.common.model.impl;

/**
 * Created by mzumbado on 2/26/15.
 */
public class Version {
    private String scmPath;
    private String md5;
    private String scmVersionName;
    private String localPath;
    private String action;
    private String source;
    private BaseVersion baseVersion;

    public Version(String scmPath, String md5, String scmVersionName, String localPath, String action, String source, BaseVersion baseVersion) {
        this.scmPath = scmPath;
        this.md5 = md5;
        this.scmVersionName = scmVersionName;
        this.localPath = localPath;
        this.action = action;
        this.source = source;
        this.baseVersion = baseVersion;
    }

    public String getScmPath() {
        return scmPath;
    }

    public void setScmPath(String scmPath) {
        this.scmPath = scmPath;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getScmVersionName() {
        return scmVersionName;
    }

    public void setScmVersionName(String scmVersionName) {
        this.scmVersionName = scmVersionName;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public BaseVersion getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(BaseVersion baseVersion) {
        this.baseVersion = baseVersion;
    }
}
