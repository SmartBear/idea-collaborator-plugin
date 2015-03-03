package com.smartbear.collab.util;

import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.diff.DiffProvider;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.smartbear.collab.common.model.CollabConstants;
import com.smartbear.collab.common.model.impl.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mzumbado on 2/26/15.
 */
public class ChangeListUtils {

    public static List<ChangeList> VcsFileRevisionToChangeList(AbstractVcs vcs, ScmToken scmToken, Map<VcsFileRevision, CommittedChangeList> commits){
        List<ChangeList> changeLists =  new ArrayList<ChangeList>();
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException nsae){}
        for (Map.Entry<VcsFileRevision, CommittedChangeList> commit : commits.entrySet()){
            VcsFileRevision fileRevision = commit.getKey();
            CommittedChangeList committedChangeList = commit.getValue();

//            String scmPath = fileRevision.getChangedRepositoryPath().toString();
            CommitInfo commitInfo = new CommitInfo(fileRevision.getCommitMessage(), fileRevision.getRevisionDate(), fileRevision.getAuthor(), false, fileRevision.getRevisionNumber().asString(), "");
            List<Version> versions = new ArrayList<Version>();
            for (Change change : committedChangeList.getChanges()){
                String fileContent = "";
                try {
                    fileContent = change.getAfterRevision().getContent();
                }
                catch (VcsException ve) {

                }
                vcs.getCommittedChangesProvider().getLocationFor(change.getAfterRevision().getFile());
                ContentRevision baseRevision = change.getBeforeRevision();
                BaseVersion baseVersion;
                if (change.getBeforeRevision() == null) {
                    baseVersion = null;
                }
                else {
                    String baseMd5 = messageDigest.digest(baseRevision.getFile().getVirtualFile().getBOM()).toString();
                    int baseVersionName = baseRevision.getRevisionNumber().asString().hashCode();
                    String baseVersionPath = baseRevision.getFile().getPath();
                    baseVersion = new BaseVersion(change.getFileStatus().getId(), baseMd5, commitInfo, CollabConstants.SOURCE_TYPE_SCM, String.valueOf(baseVersionName), "scmPath");
                }

                //Version
                String localPath = change.getVirtualFile().getPath();
                byte[] md5 = messageDigest.digest(fileContent.getBytes());
                String action = change.getFileStatus().getId();
                int scmVersionName = change.getAfterRevision().getRevisionNumber().asString().hashCode();
                Version version = new Version("scmPath", new String(md5), String.valueOf(scmVersionName), localPath, action, CollabConstants.SOURCE_TYPE_SCM, baseVersion);

                versions.add(version);
            }

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
