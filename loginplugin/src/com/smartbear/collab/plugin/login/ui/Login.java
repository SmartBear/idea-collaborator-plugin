package com.smartbear.collab.plugin.login.ui;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

import com.intellij.ide.util.PropertiesComponent;
import com.smartbear.collab.client.Client;
import com.smartbear.collab.client.exception.ClientException;
import com.smartbear.collab.client.exception.CredentialsException;
import com.smartbear.collab.client.exception.ServerURLException;

public class Login extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton testBttn;
    private JComboBox serverCmb;
    private JTextField usernameTxt;
    private JPasswordField passwordTxt;
    private JCheckBox proxyChck;
    private JButton proxyBttn;

    private Client client;
    private PropertiesComponent persistedProperties = PropertiesComponent.getInstance();

    public Login() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        testBttn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onTest();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
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

        initializeValues();
    }

    private void initializeValues(){
        if (persistedProperties.getValues("recentServers").length > 0){
            for (String recentServer : persistedProperties.getValues("recentServers")){
                serverCmb.addItem(recentServer);
            }
        }
    }

    private boolean validateFields(){
        boolean result = true;
        if (result && (serverCmb == null || serverCmb.getSelectedItem().toString().isEmpty())){
            JOptionPane.showMessageDialog(null, "Please select a server", "ERROR", JOptionPane.OK_OPTION);
            result = false;
        }
        else {
                client = new Client(serverCmb.getSelectedItem().toString());
        }
        if (result && usernameTxt.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Enter the login name", "ERROR", JOptionPane.OK_OPTION);
            result = false;
        }
        else {
            try {
                List<String> results = client.login(usernameTxt.getText(), passwordTxt.getPassword().toString());
            }
            catch (ServerURLException sue) {
                JOptionPane.showMessageDialog(null, "Invalid server " + sue.getMessage(), "ERROR", JOptionPane.OK_OPTION);
                result = false;
            }
            catch (CredentialsException ce) {
                JOptionPane.showMessageDialog(null, "Invalid credentials " + ce.getMessage(), "ERROR", JOptionPane.OK_OPTION);
                result = false;
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid general credentials " + e.getMessage(), "ERROR", JOptionPane.OK_OPTION);
                result = false;
            }
        }

        return result;
    }

    private void onOK() {
        if (validateFields()) {
            persistedProperties.setValue("username", usernameTxt.getText());
            persistedProperties.setValue("password", passwordTxt.getText());
            String[] recentServers = new String[10];
            for (int index = 0; index >= serverCmb.getItemCount(); index++){
                recentServers[index] = serverCmb.getSelectedItem().toString();
            }
            persistedProperties.setValues("servers", recentServers);
            persistedProperties.setValue("ticketId", "123");
            dispose();
        }
    }

    private void onTest() {
        if (validateFields()){
            JOptionPane.showMessageDialog(null, "Connection sucessful", "SUCESS", JOptionPane.OK_OPTION);
        }
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        Login dialog = new Login();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
