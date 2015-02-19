package com.smartbear.collab.plugin.review;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by mzumbado on 2/17/15.
 */
public class ReviewFromGitHistory extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        AddCommitsToReview dialog = new AddCommitsToReview();
        dialog.pack();
        dialog.setVisible(true);    }
}
