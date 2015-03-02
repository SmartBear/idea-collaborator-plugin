package com.smartbear.collab.util;

import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
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

    public static List<ChangeList> VcsFileRevisionToChangeList(ScmToken scmToken, Map<VcsFileRevision, CommittedChangeList> commits){
        List<ChangeList> changeLists =  new ArrayList<ChangeList>();
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException nsae){}
        for (Map.Entry<VcsFileRevision, CommittedChangeList> commit : commits.entrySet()){
            VcsFileRevision fileRevision = commit.getKey();
            CommittedChangeList committedChangeList = commit.getValue();

            CommitInfo commitInfo = new CommitInfo(fileRevision.getCommitMessage(), fileRevision.getRevisionDate(), fileRevision.getAuthor(), false, fileRevision.getRevisionNumber().asString(), "");
            List<Version> versions = new ArrayList<Version>();
            for (Change change : committedChangeList.getChanges()){
                String fileContent = "";
                try {
                    fileContent = change.getAfterRevision().getContent();
                }
                catch (VcsException ve) {

                }
                ContentRevision baseRevision = change.getBeforeRevision();
//                BaseVersion baseVersion = new BaseVersion(baseRevision.);
                //Version
                String localPath = change.getVirtualFile().getPath();
                byte[] md5 = messageDigest.digest(fileContent.getBytes());
                String action = change.getFileStatus().getId();
                int scmVersionName = change.getAfterRevision().getRevisionNumber().asString().hashCode();
                change.getType();
                Version version = new Version("scmPath", new String(md5), String.valueOf(scmVersionName), localPath, action, "source", null);

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
