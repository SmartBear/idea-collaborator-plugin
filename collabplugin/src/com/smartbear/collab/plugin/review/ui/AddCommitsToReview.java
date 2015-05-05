package com.smartbear.collab.plugin.review.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.smartbear.collab.client.Client;
import com.smartbear.collab.client.exception.ServerURLException;
import com.smartbear.collab.common.model.CollabConstants;
import com.smartbear.collab.common.model.JsonrpcCommandResponse;
import com.smartbear.collab.common.model.impl.ChangeList;
import com.smartbear.collab.common.model.impl.ScmToken;
import com.smartbear.collab.util.ChangeListUtils;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class AddCommitsToReview extends JDialog {
    private JPanel contentPane;
    private JButton cancelBttn;
    private JButton finishBttn;
    private JRadioButton createNewReviewRdBttn;
    private JRadioButton addToExistingReviewRdBttn;
    private JTextField titleTxt;
    private JButton refreshReviewsBttn;
    private JList existingReviewsLst;
    private ButtonGroup radioButtons = new ButtonGroup();

    private Client client;
    private PropertiesComponent persistedProperties = PropertiesComponent.getInstance();
    private Map<VcsFileRevision, CommittedChangeList> commits;
    private String rootDirectory;
    private ScmToken scmToken;
    private Project projectRef;

    public AddCommitsToReview(Map<VcsFileRevision, CommittedChangeList> commits, String rootDirectory, ScmToken scmToken, Project project) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(cancelBttn);
        this.setTitle("Add Commits to Review");

        this.commits = commits;
        this.rootDirectory = rootDirectory;
        this.scmToken = scmToken;
        this.projectRef = project;

        initializeClient();
        initTextTitle(commits.keySet());

        cancelBttn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        finishBttn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onFinish();
            }
        });

        refreshReviewsBttn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshReviews();
            }
        });

        radioButtons.add(createNewReviewRdBttn);
        radioButtons.add(addToExistingReviewRdBttn);

        existingReviewsLst.setAutoscrolls(true);

        addToExistingReviewRdBttn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (addToExistingReviewRdBttn.isSelected()){
                    refreshReviewsBttn.setEnabled(true);
                    existingReviewsLst.setEnabled(true);
                    titleTxt.setEnabled(false);
                    refreshReviewsBttn.doClick();
                }
                else {
                    refreshReviewsBttn.setEnabled(false);
                    existingReviewsLst.setEnabled(false);
                    titleTxt.setEnabled(false);
                }

            }
        });

        createNewReviewRdBttn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (createNewReviewRdBttn.isSelected()){
                    refreshReviewsBttn.setEnabled(false);
                    existingReviewsLst.setEnabled(false);
                    titleTxt.setEnabled(true);
                }
                else {
                    refreshReviewsBttn.setEnabled(true);
                    existingReviewsLst.setEnabled(true);
                    titleTxt.setEnabled(false);
                    refreshReviewsBttn.doClick();
                }

            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void initializeClient(){
        String serverURL = persistedProperties.getValue(CollabConstants.PROPERTY_SELECTED_SERVER);
        if (serverURL == null || serverURL.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Collaborator server URL is not set.\n\nGo to:\nSettings...\n\tTools\n\t\tSmartBear Collaborator\nand set the server parameters.", "Add to review error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
        else {
            client = new Client(serverURL);
            if (!client.hasCredentials()){
                String username = persistedProperties.getValue(CollabConstants.PROPERTY_USERNAME);
                if (!username.isEmpty()){
                    client.setUsername(username);
                }
                String ticketId = persistedProperties.getValue(CollabConstants.PROPERTY_TICKET_ID);
                if (!ticketId.isEmpty()){
                    client.setTicketId(ticketId);
                }
            }
        }
    }

    private void initTextTitle(Set<VcsFileRevision> revisions){
        String textTitle = "";
        if (revisions.size() == 1){
            VcsFileRevision revision = revisions.iterator().next();
            textTitle = revision.getRevisionNumber() + " - " + revision.getCommitMessage();
        }
        else {
            int count = 1;
            Iterator<VcsFileRevision> iterator = revisions.iterator();
            while (iterator.hasNext()){
                VcsFileRevision revision = iterator.next();
                if (count == revisions.size()){
                    textTitle = textTitle.concat(" and ");
                }
                else if (count > 1){
                    textTitle = textTitle.concat(", ");
                }
                textTitle = textTitle.concat(revision.getRevisionNumber().asString());
                count++;
            }
        }
        titleTxt.setText(textTitle);
    }

    private void onFinish() {
        String reviewId = "";
        String reviewTitle = "";
        boolean zipFilesSent = true;
        java.util.List<ChangeList> changeLists = ChangeListUtils.VcsFileRevisionToChangeList(rootDirectory, scmToken, this.commits, this.projectRef);

        if (createNewReviewRdBttn.isSelected()){
            if (titleTxt.getText() == null || titleTxt.getText().isEmpty()){
                JOptionPane.showMessageDialog(null, "Review creation error: you must give your new review a title.", "Collaborator Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                String creator = persistedProperties.getValue(CollabConstants.PROPERTY_USERNAME);
                try {
                    reviewTitle = titleTxt.getText();
                    Map<String, byte[]> zips = ChangeListUtils.getZipFiles(new ArrayList(commits.values()), scmToken);
                    for (Map.Entry<String, byte[]> zip : zips.entrySet()){
                        zipFilesSent = zipFilesSent && client.sendZip(zip);
                    }
                    JsonrpcCommandResponse response = client.createReview(creator, titleTxt.getText());
                    if (response.getErrors() == null || response.getErrors().isEmpty()) {
                        reviewId = ((Integer) response.getResult().getValue()).toString();
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Review creation error. \n\nReason:\n" + response.getErrors().get(0).getMessage(), "Collaborator Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
                catch (ServerURLException sue){
                    JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator Server \n\nReason:\n" + sue.getMessage(), "Collaborator Error", JOptionPane.ERROR_MESSAGE);
                }
                catch (Exception e){
                    JOptionPane.showMessageDialog(null, "Review creation error. \n\nReason:\n" + e.getMessage(), "Collaborator Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if (addToExistingReviewRdBttn.isSelected()){
            if (existingReviewsLst.isSelectionEmpty()){
                JOptionPane.showMessageDialog(null, "Please select a review", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                String selectedReview = (String)existingReviewsLst.getSelectedValue();
                reviewId = selectedReview.substring("Review #".length(), selectedReview.indexOf(':'));
                reviewTitle = selectedReview.substring(selectedReview.indexOf(':') + 1);
                Map<String, byte[]> zips = ChangeListUtils.getZipFiles(new ArrayList(commits.values()), scmToken);
                for (Map.Entry<String, byte[]> zip : zips.entrySet()){
                    zipFilesSent = zipFilesSent && client.sendZip(zip);
                }
            }
        }

        if (!reviewId.isEmpty() && zipFilesSent){
            try {
                JsonrpcCommandResponse addFilesResponse = client.addFilesToReview(reviewId, changeLists);
                if (addFilesResponse.getErrors() == null || addFilesResponse.getErrors().isEmpty()){
                    if (createNewReviewRdBttn.isSelected()) {
                        JOptionPane.showMessageDialog(null, "Review created, #" + reviewId + ": " + reviewTitle, "Review created", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else if (addToExistingReviewRdBttn.isSelected()) {
                        JOptionPane.showMessageDialog(null, "Changes added to review #" + reviewId + ": " + reviewTitle, "Changes added to review", JOptionPane.INFORMATION_MESSAGE);
                    }
                    dispose();
                }
            }
            catch (ServerURLException sue){
                JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator Server \n\nReason:\n" + sue.getMessage(), "Collaborator Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception e){
                if (createNewReviewRdBttn.isSelected()) {
                    JOptionPane.showMessageDialog(null, "Could not create the review. \n\nReason:\n" + e.getMessage(), "Collaborator Error", JOptionPane.ERROR_MESSAGE);
                }
                else if (addToExistingReviewRdBttn.isSelected()) {
                    JOptionPane.showMessageDialog(null, "Could not add to the review. \n\nReason:\n" + e.getMessage(), "Collaborator Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }

    private void onCancel() {
        dispose();
    }

    private void refreshReviews(){
        String username = (String)persistedProperties.getValue(CollabConstants.PROPERTY_USERNAME);
        if (username == null || username.isEmpty()){
            JOptionPane.showMessageDialog(null,  "Collaborator username is not set.\n\nGo to:\nSettings...\n\tTools\n\t\tSmartBear Collaborator\nand set the server parameters.", "Collaborator Error", JOptionPane.OK_OPTION);
            dispose();
        }
        String ticketId = (String)persistedProperties.getValue(CollabConstants.PROPERTY_TICKET_ID);
        if (ticketId == null || ticketId.isEmpty()){
            JOptionPane.showMessageDialog(null, "Collaborator auth ticket invalid.\n\nGo to:\nSettings...\n\tTools\n\t\tSmartBear Collaborator\nand test your connection.", "Collaborator Error", JOptionPane.OK_OPTION);
            dispose();
        }
        try {
            JsonrpcCommandResponse actionItems = client.getActionItems();
            java.util.List<LinkedHashMap<String, String>> aitems = (java.util.List)actionItems.getResult().getValue();
            DefaultListModel dlm = new DefaultListModel();
            for (LinkedHashMap<String, String> item : aitems){
                dlm.addElement(item.get("text").substring(item.get("text").indexOf("Review #")));
            }
            existingReviewsLst.setModel(dlm);
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, "Unable to refresh reviews: " + e.getMessage(), "Collaborator error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
