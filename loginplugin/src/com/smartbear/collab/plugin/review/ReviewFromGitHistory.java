package com.smartbear.collab.plugin.review;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.vfs.GitFileRevision;

/**
 * Created by mzumbado on 2/17/15.
 */
public class ReviewFromGitHistory extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        VcsFileRevision[] revisions = e.getData(VcsDataKeys.VCS_FILE_REVISIONS);
        GitFileRevision grv = (GitFileRevision)revisions[0];
        VirtualFile vf = e.getData(VcsDataKeys.VCS_VIRTUAL_FILE);
        vf.getName();
        VcsFileRevision vfr = e.getData(VcsDataKeys.VCS_FILE_REVISION);
        vfr.getAuthor();
        vfr.getRevisionNumber();
        vfr.getCommitMessage();
        vfr.getRevisionDate();
        AddCommitsToReview dialog = new AddCommitsToReview(revisions);
        dialog.pack();
        dialog.setVisible(true);    }
}
