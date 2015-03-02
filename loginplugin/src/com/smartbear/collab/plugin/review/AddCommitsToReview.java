package com.smartbear.collab.plugin.review;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.smartbear.collab.client.Client;
import com.smartbear.collab.common.model.CollabConstants;
import com.smartbear.collab.common.model.JsonrpcCommandResponse;
import com.smartbear.collab.common.model.impl.ChangeList;
import com.smartbear.collab.common.model.impl.ScmToken;
import com.smartbear.collab.util.ChangeListUtils;
import git4idea.vfs.GitFileRevision;

import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AddCommitsToReview extends JDialog {
    private JPanel contentPane;
    private JButton cancelBttn;
    private JButton finishBttn;
    private JButton backBttn;
    private JButton nextBttn;
    private JRadioButton createNewReviewRdBttn;
    private JRadioButton addToExistingReviewRdBttn;
    private JTextField titleTxt;
    private JButton refreshReviewsBttn;
    private JList existingReviewsLst;
    private ButtonGroup radioButtons = new ButtonGroup();

    private Client client;
    private PropertiesComponent persistedProperties = PropertiesComponent.getInstance();
    private Map<VcsFileRevision, CommittedChangeList> commits;

    public AddCommitsToReview(Map<VcsFileRevision, CommittedChangeList> commits) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(cancelBttn);

        this.commits = commits;
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
                    titleTxt.setEnabled(false);
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

    private void initTextTitle(Set<VcsFileRevision> revisions){
        String textTitle = "";
        if (revisions.size() == 1){
            VcsFileRevision revision = revisions.iterator().next();
            textTitle = revision.getRevisionNumber() + " - " + revision.getCommitMessage();
        }
        else {
            int count = 1;
            while (revisions.iterator().hasNext()){
                VcsFileRevision revision = revisions.iterator().next();
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

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException nsae){}

        java.util.List<ChangeList> changeLists = ChangeListUtils.VcsFileRevisionToChangeList(ScmToken.GIT, this.commits);

        if (createNewReviewRdBttn.isSelected()){
            if (titleTxt.getText() == null || titleTxt.getText().isEmpty()){

            }
            else {
                String creator = persistedProperties.getValue(CollabConstants.PROPERTY_USERNAME);
                try {
                    JsonrpcCommandResponse response = client.createReview(creator, titleTxt.getText());

                }
                catch (Exception e){

                }
            }
        }
        else if (addToExistingReviewRdBttn.isSelected()){

        }
        if (!reviewId.isEmpty()){

            dispose();
        }
    }

    private String getReviewTitle(){
        String result = "";
        if (createNewReviewRdBttn.isSelected()){
            if (titleTxt.getText().isEmpty()){
                JOptionPane.showMessageDialog(null, "Must provide a title for the review", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                result = titleTxt.getText();
            }
        }
        return result;
    }

    private void onCancel() {
        dispose();
    }

    private void refreshReviews(){
        String username = (String)persistedProperties.getValue(CollabConstants.PROPERTY_USERNAME);
        if (username == null || username.isEmpty()){
            JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator Server \n\nReason:\nEnter a username", "Collaborator Error", JOptionPane.OK_OPTION);
            dispose();
        }
        String ticketId = (String)persistedProperties.getValue(CollabConstants.PROPERTY_TICKET_ID);
        if (ticketId == null || ticketId.isEmpty()){
            JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator Server \n\nReason:\nEnter a username", "Collaborator Error", JOptionPane.OK_OPTION);
            dispose();
        }
        try {


            JsonrpcCommandResponse actionItems = client.getActionItems();
            java.util.List<LinkedHashMap<String, String>> aitems = (java.util.List)actionItems.getResult().getValue();
//            List cList = new List();
            DefaultListModel dlm = new DefaultListModel();
            for (LinkedHashMap<String, String> item : aitems){
                dlm.addElement(item.get("text").substring(item.get("text").indexOf("Review #")));
//                cList.add(item.get("text"));
            }
//            existingReviewsLst.add(cList)
            existingReviewsLst.setModel(dlm);
        }
        catch (Exception e){

        }
    }

    private void onAddToExistingReview(){
        if (addToExistingReviewRdBttn.isSelected()) {
            this.refreshReviews();
        } else {
            DefaultListModel defaultListModel = (DefaultListModel) existingReviewsLst.getModel();
            defaultListModel.removeAllElements();
        }
    }
/*
    public static void main(String[] args) {
        AddCommitsToReview dialog = new AddCommitsToReview();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }*/
}
