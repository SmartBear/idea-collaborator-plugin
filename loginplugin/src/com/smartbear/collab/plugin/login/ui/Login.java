package com.smartbear.collab.plugin.login.ui;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import com.intellij.ide.util.PropertiesComponent;
import com.smartbear.collab.client.Client;
import com.smartbear.collab.client.exception.ClientException;
import com.smartbear.collab.client.exception.CredentialsException;
import com.smartbear.collab.client.exception.ServerURLException;
import com.smartbear.collab.common.model.JsonrpcCommandResponse;

public class Login extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton testBttn;
    private JComboBox serverCmb;
    private JTextField usernameTxt;
    private JPasswordField passwordTxt;
    private String loginTicket = "";
    private List<String> recentServers = new ArrayList<String>();
    private JCheckBox proxyChck;
    private JButton proxyBttn;

    private Client client;
//    private PropertiesComponent persistedProperties;
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
        if (persistedProperties.getValues("recentServers") != null){
            for (String recentServer : persistedProperties.getValues("recentServers")){
                if (recentServer != null && recentServer.compareTo("null") != 0){
                    serverCmb.addItem(recentServer);
                }
            }
        }
    }

    private boolean validateFields(){
        boolean result = true;
        if (result && (serverCmb == null || serverCmb.getSelectedItem().toString().isEmpty())){
            JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator Server \n" +
                    "\n" +
                    "Reason:\n" +
                    "could not build URL", "Collaborator Error", JOptionPane.OK_OPTION);
            result = false;
        }
        else {
                client = new Client(serverCmb.getSelectedItem().toString());
        }
        if (result && usernameTxt.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator Server \n\nReason:\nEnter a username", "Collaborator Error", JOptionPane.OK_OPTION);
            result = false;
        }
        else {
            JsonrpcCommandResponse response = null;
            try {
                response = client.login(usernameTxt.getText(), new String(passwordTxt.getPassword()));
            }
            catch (ServerURLException sue) {
                JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator Server \n" +
                        "\n" +
                        "Reason:\n" +
                        "Connection refused:" + sue.getMessage(), "Collaborator Error", JOptionPane.OK_OPTION);
                result = false;
            }
            catch (CredentialsException ce) {
                JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator Server\n" +
                                "\n" +
                                "Reason:\n" +
                                "Invalid username and or password.", "Collaborator Error", JOptionPane.OK_OPTION);
                result = false;
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator Server\n" +
                        "\n" +
                        "Reason:\n" +
                        "Connection refused:" + e.getMessage(), "Collaborator Error", JOptionPane.OK_OPTION);
                result = false;
            }
            if (response.getErrors() != null && !response.getErrors().isEmpty()){
                JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator Server\n" +
                        "\n" +
                        "Reason:\n" +
                        response.getErrors().get(0).getMessage(), "Collaborator Error", JOptionPane.OK_OPTION);
                result = false;
            }
            else if (response.getResult() != null){
                loginTicket = response.getResult().getValue();
            }
        }

        return result;
    }

    private void onOK() {
        if (validateFields()) {
            persistedProperties.setValue("username", usernameTxt.getText());
            persistedProperties.setValue("password", new String(passwordTxt.getPassword()));
            if (!recentServers.contains(serverCmb.getSelectedItem().toString())){
                recentServers.add(serverCmb.getSelectedItem().toString());
            }
            String[] persistedRecentServers = new String[10];
            persistedRecentServers = recentServers.toArray(persistedRecentServers);
            persistedProperties.setValues("recentServers", persistedRecentServers);
            persistedProperties.setValue("ticketId", loginTicket);
            dispose();
        }
    }

    private void onTest() {
        if (validateFields()){
            JOptionPane.showMessageDialog(null, "Successfully connected to the Collaborator Server", "Test", JOptionPane.OK_OPTION);
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
