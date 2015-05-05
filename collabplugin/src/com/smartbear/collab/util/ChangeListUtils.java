package com.smartbear.collab.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.smartbear.collab.common.model.CollabConstants;
import com.smartbear.collab.common.model.impl.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;


import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by mzumbado on 2/26/15.
 */
public class ChangeListUtils {
    public static final Logger log = Logger.getLogger(ChangeListUtils.class.toString());

    public static List<ChangeList> VcsFileRevisionToChangeList(String rootDirectory, ScmToken scmToken, Map<VcsFileRevision, CommittedChangeList> commits, Project project) {
        List<ChangeList> changeLists =  new ArrayList<ChangeList>();
        ProjectLevelVcsManager projectLevelVcsManager = ProjectLevelVcsManager.getInstance(project);

        for (Map.Entry<VcsFileRevision, CommittedChangeList> commit : commits.entrySet()){
            VcsFileRevision fileRevision = commit.getKey();
            CommittedChangeList committedChangeList = commit.getValue();

            CommitInfo commitInfo = new CommitInfo(fileRevision.getCommitMessage(), fileRevision.getRevisionDate(), fileRevision.getAuthor(), false, fileRevision.getRevisionNumber().asString(), "");
            List<Version> versions = new ArrayList<Version>();
            String scmRepoURL = "";
            String scmRepoUUID = "";
            for (Change change : committedChangeList.getChanges()){
                String fileContent = "";
                try {
                    fileContent = change.getAfterRevision().getContent();
                }
                catch (VcsException ve) {
                    log.severe(scmToken.name() + " error: " + ve.getMessage());
                }
                ContentRevision baseRevision = change.getBeforeRevision();
                BaseVersion baseVersion;
                if (baseRevision == null) {
                    baseVersion = null;
                }
                else {
                    String baseMd5 = "";
                    String baseScmPath = getScmPath(rootDirectory, baseRevision.getFile().getPath());
                    try {
                        baseMd5 = getMD5(baseRevision.getContent().getBytes());
                    }
                    catch (VcsException ve){
                        log.severe(scmToken.name() + " error: " + ve.getMessage());
                    }
                    String baseVersionName = "";
                    baseVersionName = getScmVersionName(scmToken, baseRevision);
                    baseVersion = new BaseVersion(change.getFileStatus().getId(), baseMd5, commitInfo, CollabConstants.SOURCE_TYPE_SCM, baseVersionName, baseScmPath);
                }

                //Version
                String localPath = change.getAfterRevision().getFile().getPath();
                String scmPath = getScmPath(rootDirectory, change.getAfterRevision().getFile().getPath());
                String md5 = getMD5(fileContent.getBytes());
                String action = change.getFileStatus().getId();
                String scmVersionName = getScmVersionName(scmToken, change.getAfterRevision());
                Version version = new Version(scmPath, md5, scmVersionName, localPath, action, CollabConstants.SOURCE_TYPE_SCM, baseVersion);

                versions.add(version);

                if (scmRepoURL.equals("")) {
                    switch (scmToken) {
                        case SUBVERSION: // TODO: can probably get this in a less hacky way with svnkit, since we need that anyway now?
                            String fullPath = fileRevision.getChangedRepositoryPath().toPresentableString(); // this gives the full path down to the file, so:
                            if (fullPath.endsWith(scmPath) && fullPath.indexOf(scmPath) > 0) {
                                scmRepoURL = fullPath.substring(0, fullPath.indexOf(scmPath) - 1); // -1 to trim off trailing "/"
                            }
                            break;
                        case GIT:
                            VcsRoot vcsRoot = projectLevelVcsManager.getVcsRootObjectFor(change.getVirtualFile());
                            FileRepositoryBuilder builder = new FileRepositoryBuilder();
                            try {
                                Repository gitRepo = builder.readEnvironment().findGitDir(new File(vcsRoot.getPath().getCanonicalPath())).build();
                                Config gitConfig = gitRepo.getConfig();

                                Set<String> remotes = gitConfig.getSubsections("remote");
                                Set<String> remoteURLs = new HashSet<String>();
                                if (remotes.isEmpty()) {
                                    // TODO: figure out what existing collab clients use for git repo url for local-only situation
                                    scmRepoURL = "git: local-only";
                                } else {
                                    for (String remoteName : remotes) {
                                        remoteURLs.add(gitConfig.getString("remote", remoteName, "url"));
                                    }
                                    Iterator<String> urlitr = remoteURLs.iterator();

                                    if (remoteURLs.size() == 1) { // the easy case
                                        scmRepoURL = urlitr.next();
                                    } else {
                                        // TODO we have more than one, so figure out what the existing clients do here
                                        // for now, just grab the first one
                                        scmRepoURL = urlitr.next();
                                    }
                                }
                            } catch (Exception e) {
                                log.severe("GIT interaction error: " + e.getMessage());
                            }
                            break;
                        default:
                            log.severe("Unsupported SCM: " + scmToken);
                            break;
                    }

                }
                if (scmRepoUUID.equals("")) {
                    switch (scmToken) {
                        case SUBVERSION:
                            if (!scmRepoURL.equals("")) {
                                try {
                                    SVNURL svnURL = SVNURL.parseURIEncoded(scmRepoURL);
                                    SVNClientManager cm = SVNClientManager.newInstance();
                                    SVNWCClient workingCopyClient = cm.getWCClient();
                                    SVNInfo svnInfo = workingCopyClient.doInfo(svnURL, SVNRevision.UNDEFINED, SVNRevision.HEAD);
                                    scmRepoUUID = svnInfo.getRepositoryUUID();
                                } catch (SVNException svne) {
                                    log.severe("SVN error: " + svne.getMessage());
                                }
                            }
                            break;
                        case GIT: // for this, we use the sha1 of the first git commit in the project
                            FileRepositoryBuilder builder = new FileRepositoryBuilder();
                            try {
                                VcsRoot vcsRoot = projectLevelVcsManager.getVcsRootObjectFor(change.getVirtualFile());
                                Repository  gitRepo = builder.readEnvironment().findGitDir(new File(vcsRoot.getPath().getCanonicalPath())).build();
                                Git git = new Git(gitRepo);

                                Iterable<RevCommit> gitCommits = git.log().all().call();
                                Iterator<RevCommit> gitr = gitCommits.iterator();

                                RevCommit firstCommit = null;
                                while (gitr.hasNext()) { // run through log,
                                     firstCommit = gitr.next();
                                }
                                if (firstCommit != null) {
                                    scmRepoUUID = firstCommit.getName(); // sha1 of first commit in repo, lower-case hexadecimal
                                }
                            } catch (Exception e) {
                                log.severe("GIT interaction error: " + e.getMessage());
                            }

                            break;
                        default:
                            log.severe("Unsupported SCM: " + scmToken);
                            break;
                    }
                }
            }

            // we might have to do something more sophisticated once we support other SCMs, but for git and svn
            // collab only really needs an URL and some sort of UUID-ish value
            ArrayList<String> scmConnectionParameters = new ArrayList<String>(2);
            scmConnectionParameters.add(scmRepoURL);
            scmConnectionParameters.add(scmRepoUUID);

            ChangeList changeList = new ChangeList(scmToken, scmConnectionParameters, commitInfo, versions);
            changeLists.add(changeList);
        }
        return changeLists;
    }

    public static String getScmVersionName(ScmToken scmToken, ContentRevision revision){
        String scmVersionName = "";
        if (scmToken == ScmToken.GIT) {
            try {
                String gitHash = "blob " + revision.getContent().length() + "\0" + revision.getContent();
                scmVersionName = getSHA1(gitHash.getBytes());
            }
            catch (VcsException ve){
                log.severe(scmToken.name() + " error: " + ve.getMessage());
            }
        }
        else if (scmToken == ScmToken.SUBVERSION) {
            scmVersionName = revision.getRevisionNumber().asString();
        }
        return scmVersionName;
    }

    public static Map<String, byte[]> getZipFiles(List<CommittedChangeList> commits, ScmToken scmToken) {
        Map<String, byte[]> zips = new HashMap<String, byte[]>();
        for (CommittedChangeList commit : commits){
            String scm = scmToken.name();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);
            for (Change change : commit.getChanges()){
                if (change.getBeforeRevision() != null){
                    String baseFileContent = "";
                    try {
                        baseFileContent = change.getBeforeRevision().getContent();
                    }
                    catch (VcsException ve) {
                        log.severe(scm + " error: " + ve.getMessage());
                    }
                    String baseMd5 = getMD5(baseFileContent.getBytes());
                    ZipEntry baseZipEntry = new ZipEntry(baseMd5);

                    try {
                        zos.putNextEntry(baseZipEntry);
                        zos.write(baseFileContent.getBytes());
                        zos.closeEntry();
                    }
                    catch (IOException ioe) {
                        log.severe(ioe.getMessage());
                    }
                }
                String fileContent = "";
                try {
                    fileContent = change.getAfterRevision().getContent();
                }
                catch (VcsException ve) {
                    log.severe(scm + " error: " + ve.getMessage());
                }
                String md5 = getMD5(fileContent.getBytes());
                ZipEntry zipEntry = new ZipEntry(md5);

                try {
                    zos.putNextEntry(zipEntry);
                    zos.write(fileContent.getBytes());
                    zos.closeEntry();
                }
                catch (IOException ioe) {
                    log.severe(ioe.getMessage());
                }
            }
            try {
                zos.finish();
                zos.close();
            }
            catch (IOException ioe) {
                log.severe(ioe.getMessage());
            }
            zips.put("rev_" + Math.abs(commit.getNumber()) + ".zip", baos.toByteArray());
            try {
                baos.close();
            }
            catch (IOException ioe) {
                log.severe(ioe.getMessage());
            }
        }
        return zips;
    }

    private static String getScmPath(String root, String path){
        String result = "";
        if (path.contains(root)){
            result = path.substring(root.length() + 1);
        }
        return result;
    }

    private static String getMD5( final byte[] data ) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            log.severe("MD5 hashing algorithm is not available! This almost certainly prevents correct operation of this plugin!");
            return "";
        }
        return getHash(data, md5);
    }

    private static String getSHA1( final byte[] data ) {
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException nsae) {
            log.severe("SHA1 hashing algorithm is not available! This almost certainly prevents correct operation of this plugin!");
            return "";
        }
        return getHash(data, sha1);
    }

    private static String getHash(final byte[] data, MessageDigest algo) {
        return javax.xml.bind.DatatypeConverter.printHexBinary(algo.digest(data)).toLowerCase();
    }
}
