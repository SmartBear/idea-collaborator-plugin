package com.smartbear.collab.common.model.impl;

/**
 * Created by mzumbado on 2/26/15.
 */
public enum ScmToken {
    ACCUREV ("acc"),
    CLEARCASE ("clc"),
    CVS ("cvs"),
    GIT ("git"),
    MERCURIAL ("mer"),
    MKS ("mks"),
    NONE ("none"),
    PERFORCE ("prf"),
    RTC ("rtc"),
    SUBVERSION ("svn"),
    SURROUND ("srr"),
    SYNERGY ("syn"),
    TFS ("tfs");

    private String ideaValue;

    ScmToken(String ideaValue) {
        this.ideaValue = ideaValue;
    }

    public static ScmToken fromIdeaValue(String text) {
        if (text != null) {
            for (ScmToken b : ScmToken.values()) {
                if (text.equalsIgnoreCase(b.ideaValue)) {
                    return b;
                }
            }
        }
        return null;
    }
}
