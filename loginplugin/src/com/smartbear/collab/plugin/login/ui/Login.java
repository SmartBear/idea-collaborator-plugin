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
import com.smartbear.collab.common.model.CollabConstants;

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
    private PropertiesComponent persistedProperties = PropertiesComponent.getInstance();

    private static final int RECENT_SERVERS_SIZE = 10;

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
        if (persistedProperties.getValues(CollabConstants.PROPERTY_RECENT_SERVERS) != null){
            for (String recentServer : persistedProperties.getValues(CollabConstants.PROPERTY_RECENT_SERVERS)){
                if (recentServer != null && recentServer.compareTo("null") != 0){
                    serverCmb.addItem(recentServer);
                }
            }
        }
        if (persistedProperties.getValues(CollabConstants.PROPERTY_USERNAME) != null){
            usernameTxt.setText(persistedProperties.getValue(CollabConstants.PROPERTY_USERNAME));
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
            persistValues();
            dispose();
        }
    }

    private void persistValues(){
        persistedProperties.setValue(CollabConstants.PROPERTY_USERNAME, usernameTxt.getText());
//            persistedProperties.setValue("password", new String(passwordTxt.getPassword()));
        if (recentServers.contains(serverCmb.getSelectedItem().toString())){
            // if the server exists in the list
            if (recentServers.indexOf(serverCmb.getSelectedItem().toString()) != 0){
                // and the element is not the first in the list, the element is removed
                // in the next "if" it will be added to the beginning of the list
                recentServers.remove(serverCmb.getSelectedItem().toString());
            }
        }
        if (!recentServers.contains(serverCmb.getSelectedItem().toString())){
            // the server is added as the most recent
            recentServers.add(0,serverCmb.getSelectedItem().toString());
        }
        if (recentServers.size() > RECENT_SERVERS_SIZE){
            // keeps the recent servers up to N elements
            recentServers.remove(RECENT_SERVERS_SIZE);
        }
        String[] persistedRecentServers = new String[RECENT_SERVERS_SIZE];
        persistedRecentServers = recentServers.toArray(persistedRecentServers);
        persistedProperties.setValues(CollabConstants.PROPERTY_RECENT_SERVERS, persistedRecentServers);
        persistedProperties.setValue(CollabConstants.PROPERTY_TICKET_ID, loginTicket);
    }

    private void onTest() {
        if (validateFields()){
            JOptionPane.showMessageDialog(null, "Successfully connected to the Collaborator Server", "Test", JOptionPane.INFORMATION_MESSAGE);
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
