package com.smartbear.collab.ui.login;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Created by mzumbado on 1/27/15.
 */
public class ServerLogin extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        LoginDialog dlg = new LoginDialog();
        dlg.show();
        /*if (dlg.isOK()) {
            generateComparable(psiClass, dlg.getFields());
        }*/
    }

}
