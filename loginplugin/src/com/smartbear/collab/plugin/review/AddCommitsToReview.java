package com.smartbear.collab.plugin.review;

import com.intellij.ide.util.PropertiesComponent;
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

    private Client client;
    private PropertiesComponent persistedProperties = PropertiesComponent.getInstance();

    public AddCommitsToReview() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(cancelBttn);

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

        addToExistingReviewRdBttn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (addToExistingReviewRdBttn.isSelected()) {
                    getExistingReviews();
                } else {
                    ((DefaultListModel) existingReviewsLst.getModel()).removeAllElements();

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

    private void onFinish() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void getExistingReviews(){
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
            JsonrpcCommandResponse actionItems = client.getActionItems(username, ticketId);
            java.util.List<String> aitems = (java.util.List)actionItems.getResult().getValue();
            List cList = new List();
            for (String item : aitems){
                cList.add(item);
            }
            existingReviewsLst.add(cList);
        }
        catch (Exception e){

        }
    }

    public static void main(String[] args) {
        AddCommitsToReview dialog = new AddCommitsToReview();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
