package com.smartbear.collab.plugin.review;

import javax.swing.*;
import java.awt.event.*;

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

    public AddCommitsToReview() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(cancelBttn);

        cancelBttn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        finishBttn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
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

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        AddCommitsToReview dialog = new AddCommitsToReview();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
