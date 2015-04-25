package com.smartbear.collab.plugin.login.ui;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.Disposer;
import com.smartbear.collab.client.Client;
import com.smartbear.collab.client.exception.ClientException;
import com.smartbear.collab.client.exception.CredentialsException;
import com.smartbear.collab.client.exception.ServerURLException;
import com.smartbear.collab.common.model.JsonrpcCommandResponse;
import com.smartbear.collab.common.model.CollabConstants;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class Login implements Configurable {
    private JPanel contentPane;
    private JButton testBttn;
    private JComboBox serverCmb;
    private JTextField usernameTxt;
    private JPasswordField passwordTxt;
    private String loginTicket = "";
    private List<String> recentServers = new ArrayList<String>();
    private JCheckBox proxyChck;
    private JButton proxyBttn;
    private JButton refreshAuthTicketButton;

    private Client client;
    private PropertiesComponent persistedProperties = PropertiesComponent.getInstance();
    private boolean passwordChanged = false;

    private static final int RECENT_SERVERS_SIZE = 10;

    @Override
    public void disposeUIResources() {
    }

    @Override
    public void reset() {
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Server Login";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        initializeUI();
        return contentPane;
    }

    @Override
    public void apply() throws ConfigurationException {
        if (validateFields()) {
            persistValues();
            passwordChanged = false;
        }
        else {
            passwordChanged = false;
            throw new ConfigurationException("Error authenticating with the server.");
        }
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public boolean isModified() {
        boolean result = false;
        if (persistedProperties.getValue(CollabConstants.PROPERTY_SELECTED_SERVER) == null || persistedProperties.getValue(CollabConstants.PROPERTY_SELECTED_SERVER).isEmpty()){
            if (serverCmb.getSelectedItem() == null || serverCmb.getSelectedItem().toString().isEmpty()){
                result = result || false;
            }
            else {
                result = result || true;
            }
        }
        else {
            if (serverCmb.getSelectedItem() == null || serverCmb.getSelectedItem().toString().isEmpty()){
                result = result || false;
            }
            else {
                result = result || persistedProperties.getValue(CollabConstants.PROPERTY_SELECTED_SERVER).compareTo(serverCmb.getSelectedItem().toString()) != 0;
            }
        }
        if (persistedProperties.getValue(CollabConstants.PROPERTY_USERNAME) == null || persistedProperties.getValue(CollabConstants.PROPERTY_USERNAME).isEmpty()){
            if (usernameTxt.getText() == null || usernameTxt.getText().isEmpty()){
                result = result || false;
            }
            else {
                result = result || true;
            }
        }
        else {
            result = result || persistedProperties.getValue(CollabConstants.PROPERTY_USERNAME).compareTo(usernameTxt.getText()) != 0;
        }
        result = result || passwordChanged;
        return result;
    }

    private void initializeUI() {

        testBttn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onTest();
            }
        });

        refreshAuthTicketButton.addActionListener(new ActionListener() {
                                                      @Override
                                                      public void actionPerformed(ActionEvent e) {
                                                          onRefresh();
                                                      }
        });

        passwordTxt.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                passwordChanged = true;
            }
            @Override
            public void keyPressed(KeyEvent e) {
                //Do Nothing
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //Do Nothing
            }
        });

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
        if (result && (serverCmb == null || serverCmb.getSelectedItem() == null || serverCmb.getSelectedItem().toString().isEmpty())){
            JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator server \n" +
                    "\n" +
                    "Reason:\n" +
                    "could not build URL", "Collaborator Error", JOptionPane.ERROR_MESSAGE);
            result = false;
        }
        else {
                client = new Client(serverCmb.getSelectedItem().toString());
        }
        if (result && usernameTxt.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator server \n\nReason:\nEnter a username", "Collaborator Error", JOptionPane.ERROR_MESSAGE);
            result = false;
        }
        else {
            JsonrpcCommandResponse response = null;
            try {
                response = client.login(usernameTxt.getText(), new String(passwordTxt.getPassword()));
            }
            catch (ServerURLException sue) {
                JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator server \n" +
                        "\n" +
                        "Reason:\n" +
                        "Connection refused:" + sue.getMessage(), "Collaborator Error", JOptionPane.ERROR_MESSAGE);
                result = false;
            }
            catch (CredentialsException ce) {
                JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator server\n" +
                                "\n" +
                                "Reason:\n" +
                                ce.getMessage(), "Collaborator Error", JOptionPane.ERROR_MESSAGE);
                result = false;
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator server\n" +
                        "\n" +
                        "Reason:\n" +
                        "Connection refused:" + e.getMessage(), "Collaborator Error", JOptionPane.ERROR_MESSAGE);
                result = false;
            }
            if (result) {
                if (response.getErrors() != null && !response.getErrors().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Could not verify connection to Collaborator server\n" +
                            "\n" +
                            "Reason:\n" +
                            response.getErrors().get(0).getMessage(), "Collaborator Error", JOptionPane.ERROR_MESSAGE);
                    result = false;
                } else if (response.getResult() != null) {
                    loginTicket = (String) response.getResult().getValue();
                }
            }
        }

        return result;
    }

    private void persistValues(){
        persistedProperties.setValue(CollabConstants.PROPERTY_USERNAME, usernameTxt.getText());
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
            persistedProperties.setValue(CollabConstants.PROPERTY_SELECTED_SERVER, serverCmb.getSelectedItem().toString());
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
            JOptionPane.showMessageDialog(null, "Successfully connected to the Collaborator server", "Test", JOptionPane.INFORMATION_MESSAGE);
            persistValues();
        }
    }

    private void onRefresh() {
        if (validateFields()) {
            JOptionPane.showMessageDialog(null, "Successfully connected to the Collaborator server", "Auth ticket refreshed", JOptionPane.INFORMATION_MESSAGE);
            persistValues();
        }
    }
}
