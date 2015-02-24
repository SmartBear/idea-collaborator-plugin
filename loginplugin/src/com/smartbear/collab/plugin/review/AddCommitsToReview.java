package com.smartbear.collab.plugin.review;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.smartbear.collab.client.Client;
import com.smartbear.collab.common.model.CollabConstants;
import com.smartbear.collab.common.model.JsonrpcCommandResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
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

    public AddCommitsToReview(VcsFileRevision[] revisions) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(cancelBttn);

        initTextTitle(revisions);

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

    private void initTextTitle(VcsFileRevision[] revisions){
        String textTitle = "";
        if (revisions.length == 1){
            textTitle = revisions[0].getRevisionNumber() + " - " + revisions[0].getCommitMessage();
        }
        else {
            for (int idx = 0; idx < revisions.length; idx++){
                if (idx > 0 && idx < revisions.length - 1){
                    textTitle = textTitle.concat(",");
                }
                else if (idx == revisions.length - 1){
                    textTitle = textTitle.concat(" and ");
                }
                textTitle = textTitle.concat(revisions[idx].getRevisionNumber().asString());
            }
        }
        titleTxt.setText(textTitle);
    }

    private void onFinish() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
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
            client = new Client("http://localhost:8080");

            JsonrpcCommandResponse actionItems = client.getActionItems(username, ticketId);
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
