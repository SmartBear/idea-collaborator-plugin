package com.smartbear.collab.plugin.login;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.smartbear.collab.plugin.login.ui.Login;

/**
 * Created by miguelon on 2/11/15.
 */
public class LoginActn extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        Login login = new Login();
        login.pack();
        login.setVisible(true);
    }
}
