package com.smartbear.collab.plugin;

import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by miguelon on 3/19/15.
 */
public class appcomptest implements ApplicationComponent {
    public appcomptest() {
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "appcomptest";
    }
}
