/*
   Copyright 2015 SmartBear Software, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.smartbear.collab.plugin.review;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.history.*;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.smartbear.collab.client.Client;
import com.smartbear.collab.common.model.CollabConstants;
import com.smartbear.collab.common.model.impl.ScmToken;
import com.smartbear.collab.plugin.review.ui.AddCommitsToReview;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by mzumbado on 2/17/15.
 */
public class ReviewFromScmHistory extends AnAction {
    private PropertiesComponent persistedProperties = PropertiesComponent.getInstance();

    public void actionPerformed(AnActionEvent e) {
        VcsFileRevision[] revisions = e.getData(VcsDataKeys.VCS_FILE_REVISIONS);
        if (revisions.length == 0) {
            JOptionPane.showMessageDialog(null, "No revisions found for " + VcsDataKeys.VCS_FILE_REVISIONS, "Add to review error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Map<VcsFileRevision, CommittedChangeList> changesMap = new LinkedHashMap<VcsFileRevision, CommittedChangeList>();
        VirtualFile anyFile = null;
        for (VcsFileRevision vcsFileRevision : revisions){
            CommittedChangeList committedChangeList = getCommittedChangeList(e.getProject(), e.getDataContext(), vcsFileRevision.getRevisionNumber());
            changesMap.put(vcsFileRevision, committedChangeList);
            if (anyFile == null){
                anyFile = committedChangeList.getChanges().iterator().next().getVirtualFile();
            }
        }

        VcsKey vcsKey = VcsDataKeys.VCS.getData(e.getDataContext());
        ScmToken scmToken = ScmToken.fromIdeaValue(vcsKey.getName());
        if (checkClient()) {
            ProjectLevelVcsManager projectLevelVcsManager = ProjectLevelVcsManager.getInstance(e.getProject());
            VirtualFile vcsRoot = projectLevelVcsManager.getVcsRootFor(anyFile);
            if (vcsRoot == null) {
                JOptionPane.showMessageDialog(null, "Can't get SCM root for: " + anyFile.getCanonicalPath(), "Add to review error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String rootPath = vcsRoot.getCanonicalPath();

            AddCommitsToReview dialog = new AddCommitsToReview(changesMap, rootPath, scmToken, e.getProject());
            dialog.pack();
            dialog.setVisible(true);
        }
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
                JOptionPane.showMessageDialog(null, vcsKey.getName() + " error: " + vce.getMessage(), "SCM Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        return result;
    }

    private boolean checkClient(){
        String serverURL = persistedProperties.getValue(CollabConstants.PROPERTY_SELECTED_SERVER);
        if (serverURL == null || serverURL.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Collaborator server URL is not set.\n\nGo to:\nSettings...\n\tTools\n\t\tSmartBear Collaborator\nand set the server parameters.", "Add to review error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else {
            Client client = new Client(serverURL);
            if (!client.hasCredentials()){
                String username = persistedProperties.getValue(CollabConstants.PROPERTY_USERNAME);
                if (username != null && !username.isEmpty()){
                    client.setUsername(username);
                }
                else {
                    JOptionPane.showMessageDialog(null, "Collaborator username is not set.\n\nGo to:\nSettings...\n\tTools\n\t\tSmartBear Collaborator\nand set the server parameters.", "Add to review error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                String ticketId = persistedProperties.getValue(CollabConstants.PROPERTY_TICKET_ID);
                if (ticketId != null && !ticketId.isEmpty()){
                    client.setTicketId(ticketId);
                }
                else {
                    JOptionPane.showMessageDialog(null, "Collaborator auth ticket invalid.\n\nGo to:\nSettings...\n\tTools\n\t\tSmartBear Collaborator\nand test your connection.", "Add to review error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                try {
                    client.checkTicket();
                }
                catch (Exception e){
                    JOptionPane.showMessageDialog(null, "Unable to authenticate with Collaborator server. Reason:\n\n" + e.getMessage(), "Add to review error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "Collaborator connection properties not set.\n\nGo to:\nSettings...\n\tTools\n\t\tSmartBear Collaborator\nand set the server parameters.", "Add to review error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return true;
    }
}
