package com.smartbear.collab.plugin.review;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.annotate.ShowAllAffectedGenericAction;
import com.intellij.openapi.vcs.changes.ChangeList;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.diff.DiffProvider;
import com.intellij.openapi.vcs.history.*;
import com.intellij.openapi.vcs.versionBrowser.ChangeBrowserSettings;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.actions.ShowAllSubmittedFiles;
import git4idea.vfs.GitFileRevision;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mzumbado on 2/17/15.
 */
public class ReviewFromGitHistory extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        VcsFileRevision[] revisions = e.getData(VcsDataKeys.VCS_FILE_REVISIONS);
        Map<VcsFileRevision, CommittedChangeList> changesMap = new LinkedHashMap<VcsFileRevision, CommittedChangeList>();
        for (VcsFileRevision vcsFileRevision : revisions){
            CommittedChangeList committedChangeList = getCommittedChangeList(e.getProject(), e.getDataContext(), vcsFileRevision.getRevisionNumber());
            changesMap.put(vcsFileRevision, committedChangeList);
        }
        AddCommitsToReview dialog = new AddCommitsToReview(changesMap);
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
            DiffProvider diffProvider = vcs.getDiffProvider();
            VcsRevisionNumber revision1 = diffProvider.getCurrentRevision(virtualFile);
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

    private CommittedChangeList getRemoteList(AbstractVcs vcs, VcsRevisionNumber revision, VirtualFile nonLocal) throws VcsException {
        CommittedChangesProvider provider = vcs.getCommittedChangesProvider();
        RepositoryLocation local = provider.getForNonLocal(nonLocal);
        if(local != null) {
            String number = revision.asString();
            ChangeBrowserSettings settings = provider.createDefaultSettings();
            List changes = provider.getCommittedChanges(settings, local, provider.getUnlimitedCountValue());
            if(changes != null) {
                Iterator i$ = changes.iterator();

                while(i$.hasNext()) {
                    CommittedChangeList change = (CommittedChangeList)i$.next();
                    if(number.equals(String.valueOf(change.getNumber()))) {
                        return change;
                    }
                }
            }
        }

        return null;
    }


}
