package com.smartbear.collab.util;

import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.smartbear.collab.common.model.impl.ChangeList;
import com.smartbear.collab.common.model.impl.CommitInfo;
import com.smartbear.collab.common.model.impl.ScmToken;
import com.smartbear.collab.common.model.impl.Version;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mzumbado on 2/26/15.
 */
public class ChangeListUtils {

    public static List<ChangeList> VcsFileRevisionToChangeList(ScmToken scmToken, VcsFileRevision[] vcsFileRevisions){
        List<ChangeList> changeLists =  new ArrayList<ChangeList>();
        for (VcsFileRevision vcsFileRevision : vcsFileRevisions){
            CommitInfo commitInfo = new CommitInfo(vcsFileRevision.getCommitMessage(), vcsFileRevision.getRevisionDate(), vcsFileRevision.getAuthor(), false, vcsFileRevision.getRevisionNumber().asString(), "");
            List<Version> versions = new ArrayList<Version>();
            ChangeList changeList = new ChangeList(scmToken, getConnectionParameters(scmToken), commitInfo, versions);
            changeLists.add(changeList);
        }
        return changeLists;
    }

    private static List<String> getConnectionParameters(ScmToken scmToken){
        List<String> result = new ArrayList<String>();
        if (scmToken == ScmToken.GIT){
            String currentdirectory = "";
            String globalprovider = "git";
            String scm = "git";
            String gitexe = "";
            result.add(currentdirectory);
            result.add(globalprovider);
            result.add(scm);
            result.add(gitexe);
        }
        return result;
    }
}
