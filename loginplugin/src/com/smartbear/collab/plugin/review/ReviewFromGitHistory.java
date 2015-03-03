package com.smartbear.collab.plugin.review;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.history.*;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by mzumbado on 2/17/15.
 */
public class ReviewFromGitHistory extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        VcsFileRevision[] revisions = e.getData(VcsDataKeys.VCS_FILE_REVISIONS);
        Map<VcsFileRevision, CommittedChangeList> changesMap = new LinkedHashMap<VcsFileRevision, CommittedChangeList>();
        VirtualFile anyFile = null;
        for (VcsFileRevision vcsFileRevision : revisions){
            CommittedChangeList committedChangeList = getCommittedChangeList(e.getProject(), e.getDataContext(), vcsFileRevision.getRevisionNumber());
            changesMap.put(vcsFileRevision, committedChangeList);
            if (anyFile == null){
                anyFile = committedChangeList.getChanges().iterator().next().getVirtualFile();
            }
        }

        ProjectLevelVcsManager projectLevelVcsManager = ProjectLevelVcsManager.getInstance(e.getProject());
        String rootPath = projectLevelVcsManager.getVcsRootFor(anyFile).getCanonicalPath();

        AddCommitsToReview dialog = new AddCommitsToReview(changesMap, rootPath);
        dialog.pack();
        dialog.setVisible(true);
    }

    private CommittedChangeList getCommittedChangeList(Project project, DataContext dataContext, VcsRevisionNumber revisionNumber){
        CommittedChangeList result = null;
        VcsKey vcsKey = VcsDataKeys.VCS.getData(dataContext);
        boolean isNonLocal = VcsDataKeys.VCS_NON_LOCAL_HISTORY_SESSION.getData(dataContext);
        VirtualFile virtualFile = VcsDataKeys.VCS_VIRTUAL_FILE.getData(dataContext);

        final AbstractVcs vcs = ProjectLevelVcsManager.getInstance(project).findVcsByName(vcsKey.getName());
        if (!isNonLocal) {
            CommittedChangesProvider provider = vcs.getCommittedChangesProvider();
            try {
                final Pair<CommittedChangeList, FilePath> pair = provider.getOneList(virtualFile, revisionNumber);
                if (pair != null) {
                    result = pair.getFirst();
                }
            }
            catch (VcsException vce){

            }
        }

        return result;
    }

}
