package Fridfiltrator.ui;

import Fridfiltrator.poller.Poller;
import Fridfiltrator.poller.CollabPoller;
import Fridfiltrator.helpers.ApiWrapper;
import Fridfiltrator.helpers.Logger;
import Fridfiltrator.helpers.Storage;
import Fridfiltrator.helpers.Storage.SERVER_TYPE;
import Fridfiltrator.poller.ListenerPoller;
import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.SecretKey;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import java.awt.event.ItemEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

public class TabGUI extends javax.swing.JPanel {

    private final Logger log;
    private final Storage storage;
    private final ApiWrapper api;
    private Poller poller = null;

    /**
     * Creates new form NewJPanel
     */
    public TabGUI () {

        this.log = Logger.getLogger ();
        this.storage = Storage.getInstance ();
        this.api = ApiWrapper.getInstance ();
        
        api.getApis ().extension ().registerUnloadingHandler (
            new ExtensionUnloadHandler ()
        );

        initComponents ();
        
        initValues ();
    }
    
    /**
     * Loads the appropriate stored configuration values into the UI, or creates
     * default ones when needed.
     */
    private void initValues () {
        
        // Selected server type
        SERVER_TYPE serverType = storage.getServerType ();
        serverTypeComboBox.setSelectedItem (serverType);

        switch (serverType) {
        
            case COLLABORATOR:
                // Collaborator key
                String collabKey = storage.getCollaboratorKey ();

                if (collabKey == null) {

                    collabKey = generateNewCollaboratorKey ();
                    storage.setCollaboratorKey (collabKey);
                }
                collaboratorKeyTextField.setText (collabKey);   
                break;
            
            case SELF_HOSTED:
                
                listenerPortSpinner.setValue (
                        storage.getListenerPort () // always returns a valid int
                );
                
                InetAddress addr = storage.getListenerAddr ();
                
                if (addr != null) {

                    listenerAddrTextField.setText (addr.getHostAddress ());
                }

                break;
        }

        /* Auto-scroll */
        new SmartScroller (outputScrollPane);
        new SmartScroller (debugPollingScrollPane);
        new SmartScroller (healthCheckScrollPane);
    }

    /**
     * Generates a new base64-encoded Collaborator secret key, which is used
     * for polling.
     */
    private String generateNewCollaboratorKey () {
        
        CollaboratorClient collabClient = api.getApis ()
                                            .collaborator ()
                                            .createClient ();

        SecretKey key = collabClient.getSecretKey ();
        log.log ("Generated a new collaborator secret key: " + key.toString ());

        return key.toString ();
    }

    /**
     * Uses the configured data to instantiate a new Poller by modifying the
     * {@link TabGUI#poller} attribute.
     * 
     * @throws IllegalArgumentException
     *              If the configuration is not correct and the poller couldn't
     *          be initialised.
     */
    private void reloadPoller () throws IllegalArgumentException {

        if ( (poller != null)
            && poller.isRunning ()  ) {

            poller.stop ();
        }

        /* The healthcheck must be verified differently if it's a self-hosted
        server or if it's a Collaborator server */
        SERVER_TYPE serverType = (SERVER_TYPE)serverTypeComboBox.getSelectedItem ();

        switch (serverType) {

            case COLLABORATOR:

                String key = storage.getCollaboratorKey ();

                this.poller = new CollabPoller (key);
                break;

            case SELF_HOSTED:
                
                InetAddress addr = storage.getListenerAddr ();
                int port = storage.getListenerPort ();

                this.poller = new ListenerPoller (addr, port);
                break;

            default:
                throw new IllegalArgumentException ("Error!\nServer of type " +
                        serverTypeComboBox.getSelectedItem ().toString () +
                        " is not implemented!"
                );
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        configTab = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel6 = new javax.swing.JPanel();
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(15, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(15, 32767));
        jLabel8 = new javax.swing.JLabel();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 30), new java.awt.Dimension(0, 30), new java.awt.Dimension(32767, 30));
        genBundleButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jPanel3 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        filler40 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 32767));
        jLabel6 = new javax.swing.JLabel();
        filler39 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jSeparator1 = new javax.swing.JSeparator();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel4 = new javax.swing.JPanel();
        filler36 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel4 = new javax.swing.JLabel();
        filler38 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        serverTypeComboBox = new javax.swing.JComboBox<>();
        filler34 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        serverTypeTabPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        filler20 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jLabel1 = new javax.swing.JLabel();
        filler43 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        collaboratorKeyTextField = new javax.swing.JTextField();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        setCollabKeyButton = new javax.swing.JButton();
        filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel12 = new javax.swing.JPanel();
        filler18 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        genCollabKeyButton = new javax.swing.JButton();
        filler19 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel2 = new javax.swing.JPanel();
        filler32 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        jPanel7 = new javax.swing.JPanel();
        filler41 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jLabel2 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        listenerAddrTextField = new javax.swing.JTextField();
        filler28 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        setAddrButton = new javax.swing.JButton();
        filler35 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler31 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        jPanel8 = new javax.swing.JPanel();
        filler42 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jLabel3 = new javax.swing.JLabel();
        filler29 = new javax.swing.Box.Filler(new java.awt.Dimension(33, 0), new java.awt.Dimension(33, 0), new java.awt.Dimension(33, 32767));
        listenerPortSpinner = new javax.swing.JSpinner();
        filler30 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        setPortButton = new javax.swing.JButton();
        filler37 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler33 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        filler44 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jPanel5 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0));
        jLabel7 = new javax.swing.JLabel();
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        runHealthCheckButton = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jLabel5 = new javax.swing.JLabel();
        healthCheckScrollPane = new javax.swing.JScrollPane();
        healthCheckTextArea = new javax.swing.JTextArea();
        outputTab = new javax.swing.JPanel();
        outputScrollPane = new javax.swing.JScrollPane();
        outputTextArea = new javax.swing.JTextArea();
        jPanel13 = new javax.swing.JPanel();
        startPollerButton = new javax.swing.JButton();
        stopPollerButton = new javax.swing.JButton();
        debugPollingScrollPane = new javax.swing.JScrollPane();
        debugPollingTextArea = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout());

        configTab.setLayout(new java.awt.GridLayout(2, 2, 30, 30));

        jTextPane1.setContentType("text/html"); // NOI18N
        jTextPane1.setText("<html>                                                                                    \n  <head>                                                                                  \n  </head>                                                                                 \n  <body>                                                                                  \n    <h1>Instructions</h1>\n    <ol>\n        <li>\n            Select the server type (either \"collaborator\" or \"self-hosted\")\n            <ul>\n                <li>If selecting \"<i>Collaborator</i>\", make sure you <b>save the key</b>. Without it, you won't be able to retrieve the events.</li>\n                <li>If, on the other hand, you select \"<i>self-hosted</i>\", the IP address (or FQDN) and port must be configured to point to your listener.</li>\n            </ul>\n        </li>\n        <li>\n            Verify that the server is working (at least, from your workstation) by clicking on \"<b>Healthcheck</b>\".\n        </li>\n        <br/>\n        <li>\n            Generate the bundle to install on the application side by clicking on \"<b>Generate Bundle</b>\".<br/>\n        Follow the instructions inside its <b>README.md</b> to run it and check if it's working.\n        </li>\n        <br/>\n        <li>\n            Happy testing! :)\n        </li>\n    </ol>\n    <hr/>\n\n    For more info, please refer to <a href=\"https://github.com/Foo-Manroot/fridfiltrator/blob/main/README.md\">https://github.com/Foo-Manroot/fridfiltrator/blob/main/README.md</a>\n  </body>\n</html> ");
        jScrollPane2.setViewportView(jTextPane1);

        configTab.add(jScrollPane2);

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.Y_AXIS));
        jPanel6.add(filler14);

        jLabel8.setFont(new java.awt.Font("Noto Sans", 1, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(204, 0, 0));
        jLabel8.setText("Step 3: generate bundle");
        jPanel6.add(jLabel8);
        jPanel6.add(filler13);

        genBundleButton.setText("Generate bundle");
        genBundleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genBundleButtonActionPerformed(evt);
            }
        });
        jPanel6.add(genBundleButton);
        jPanel6.add(filler1);

        configTab.add(jPanel6);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        jPanel9.setLayout(new javax.swing.BoxLayout(jPanel9, javax.swing.BoxLayout.LINE_AXIS));
        jPanel9.add(filler40);

        jLabel6.setFont(new java.awt.Font("Noto Sans", 1, 13)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(204, 0, 0));
        jLabel6.setText("Step 1: Configure the server type");
        jPanel9.add(jLabel6);
        jPanel9.add(filler39);

        jPanel3.add(jPanel9);
        jPanel3.add(filler9);
        jPanel3.add(jSeparator1);
        jPanel3.add(filler10);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));
        jPanel4.add(filler36);

        jLabel4.setText("Server type:");
        jPanel4.add(jLabel4);
        jPanel4.add(filler38);

        serverTypeComboBox.setModel(new DefaultComboBoxModel<>(SERVER_TYPE.values ()));
        serverTypeComboBox.setMaximumSize(new java.awt.Dimension(32767, 25));
        serverTypeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                serverTypeComboBoxItemStateChanged(evt);
            }
        });
        jPanel4.add(serverTypeComboBox);
        jPanel4.add(filler34);

        jPanel3.add(jPanel4);
        jPanel3.add(filler8);

        serverTypeTabPane.setEnabled(false);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jPanel11.setLayout(new javax.swing.BoxLayout(jPanel11, javax.swing.BoxLayout.LINE_AXIS));
        jPanel11.add(filler20);

        jLabel1.setText("Collaborator key:");
        jLabel1.setPreferredSize(new java.awt.Dimension(106, 20));
        jPanel11.add(jLabel1);
        jPanel11.add(filler43);

        collaboratorKeyTextField.setMaximumSize(new java.awt.Dimension(200, 25));
        collaboratorKeyTextField.setPreferredSize(new java.awt.Dimension(350, 25));
        jPanel11.add(collaboratorKeyTextField);
        jPanel11.add(filler6);

        setCollabKeyButton.setText("Set value");
        setCollabKeyButton.setMaximumSize(new java.awt.Dimension(86, 20));
        setCollabKeyButton.setMinimumSize(new java.awt.Dimension(86, 20));
        setCollabKeyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setCollabKeyButtonActionPerformed(evt);
            }
        });
        jPanel11.add(setCollabKeyButton);
        jPanel11.add(filler17);

        jPanel1.add(jPanel11);

        jPanel12.setLayout(new javax.swing.BoxLayout(jPanel12, javax.swing.BoxLayout.LINE_AXIS));
        jPanel12.add(filler18);

        genCollabKeyButton.setText("Generate new key");
        genCollabKeyButton.setMaximumSize(new java.awt.Dimension(196, 20));
        genCollabKeyButton.setMinimumSize(new java.awt.Dimension(196, 20));
        genCollabKeyButton.setPreferredSize(new java.awt.Dimension(196, 25));
        genCollabKeyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genCollabKeyButtonActionPerformed(evt);
            }
        });
        jPanel12.add(genCollabKeyButton);
        jPanel12.add(filler19);

        jPanel1.add(jPanel12);
        jPanel1.add(filler5);

        serverTypeTabPane.addTab("Collaborator", jPanel1);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));
        jPanel2.add(filler32);

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.LINE_AXIS));
        jPanel7.add(filler41);

        jLabel2.setText("Listener address:");
        jPanel7.add(jLabel2);
        jPanel7.add(filler2);

        listenerAddrTextField.setMaximumSize(new java.awt.Dimension(200, 25));
        listenerAddrTextField.setPreferredSize(new java.awt.Dimension(200, 25));
        jPanel7.add(listenerAddrTextField);
        jPanel7.add(filler28);

        setAddrButton.setText("Set value");
        setAddrButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setAddrButtonActionPerformed(evt);
            }
        });
        jPanel7.add(setAddrButton);
        jPanel7.add(filler35);

        jPanel2.add(jPanel7);
        jPanel2.add(filler31);

        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));
        jPanel8.add(filler42);

        jLabel3.setText("Listener port:");
        jPanel8.add(jLabel3);
        jPanel8.add(filler29);

        listenerPortSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 65535, 1));
        listenerPortSpinner.setMaximumSize(new java.awt.Dimension(200, 25));
        listenerPortSpinner.setMinimumSize(new java.awt.Dimension(200, 25));
        listenerPortSpinner.setPreferredSize(new java.awt.Dimension(200, 25));
        jPanel8.add(listenerPortSpinner);
        jPanel8.add(filler30);

        setPortButton.setText("Set value");
        setPortButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setPortButtonActionPerformed(evt);
            }
        });
        jPanel8.add(setPortButton);
        jPanel8.add(filler37);

        jPanel2.add(jPanel8);
        jPanel2.add(filler33);

        serverTypeTabPane.addTab("Self-hosted", jPanel2);

        jPanel3.add(serverTypeTabPane);
        jPanel3.add(filler11);
        jPanel3.add(filler44);

        configTab.add(jPanel3);

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));

        jPanel10.setPreferredSize(new java.awt.Dimension(888, 20));
        jPanel10.setLayout(new javax.swing.BoxLayout(jPanel10, javax.swing.BoxLayout.LINE_AXIS));
        jPanel10.add(filler3);

        jLabel7.setFont(new java.awt.Font("Noto Sans", 1, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(204, 0, 0));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("Step 2: verify config");
        jPanel10.add(jLabel7);
        jPanel10.add(filler15);

        jPanel5.add(jPanel10);
        jPanel5.add(filler12);

        runHealthCheckButton.setText("Healthcheck");
        runHealthCheckButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runHealthCheckButtonActionPerformed(evt);
            }
        });
        jPanel5.add(runHealthCheckButton);
        jPanel5.add(filler7);

        jLabel5.setText("Debug output:");
        jPanel5.add(jLabel5);

        healthCheckTextArea.setColumns(20);
        healthCheckTextArea.setRows(5);
        healthCheckScrollPane.setViewportView(healthCheckTextArea);

        jPanel5.add(healthCheckScrollPane);

        configTab.add(jPanel5);

        jTabbedPane2.addTab("Configuration", configTab);

        outputTab.setLayout(new java.awt.BorderLayout());

        outputTextArea.setEditable(false);
        outputTextArea.setColumns(20);
        outputTextArea.setRows(5);
        outputTextArea.setText("Here will appear application interactions appear (if there are any):\n\n");
        outputTextArea.setMargin(new java.awt.Insets(0, 0, 0, 0));
        outputScrollPane.setViewportView(outputTextArea);

        outputTab.add(outputScrollPane, java.awt.BorderLayout.CENTER);

        startPollerButton.setText("Start poller");
        startPollerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startPollerButtonActionPerformed(evt);
            }
        });
        jPanel13.add(startPollerButton);

        stopPollerButton.setText("Stop poller");
        stopPollerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopPollerButtonActionPerformed(evt);
            }
        });
        jPanel13.add(stopPollerButton);

        outputTab.add(jPanel13, java.awt.BorderLayout.NORTH);

        debugPollingTextArea.setColumns(20);
        debugPollingTextArea.setRows(5);
        debugPollingTextArea.setText("Debug output:\n");
        debugPollingTextArea.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        debugPollingScrollPane.setViewportView(debugPollingTextArea);

        outputTab.add(debugPollingScrollPane, java.awt.BorderLayout.PAGE_END);

        jTabbedPane2.addTab("Output", outputTab);

        add(jTabbedPane2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void serverTypeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_serverTypeComboBoxItemStateChanged

        if (evt.getStateChange () != ItemEvent.SELECTED) {
            return;
        }

        int selectedIdx = serverTypeComboBox.getSelectedIndex ();
        SERVER_TYPE selectedType = serverTypeComboBox.getItemAt (selectedIdx);
        
        // tabCount starts at 1, whereas tabIndex starts at 0 (hence, >=)
        if (selectedIdx >= serverTypeTabPane.getTabCount ()) {

            String errMsg = "The selected item on serverTypeComboBox was " +
                    selectedIdx + " (" + selectedType + "); but " +
                    "serverTypeTabPane has only " +
                    serverTypeTabPane.getTabCount () + " tabs.";

            log.error (errMsg);
            JOptionPane.showMessageDialog (this, "ERROR!\n" + errMsg);
            return;
        }

        serverTypeTabPane.setSelectedIndex (selectedIdx);
        storage.setServerType (selectedType);
//        log.log ("Server type selected: " + selectedType.friendlyName ());
    }//GEN-LAST:event_serverTypeComboBoxItemStateChanged

    private void setCollabKeyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setCollabKeyButtonActionPerformed
        
        String collaboratorKey = collaboratorKeyTextField.getText ();
        
        if ( ! collaboratorKey.isBlank ()) {

            storage.setCollaboratorKey (collaboratorKey);
            log.log ("New collaborator key configured: " + collaboratorKey);
        } else {

            storage.setCollaboratorKey (null); // Delete the entry
            log.log ("Collaborator key deleted from storage");
        }

    }//GEN-LAST:event_setCollabKeyButtonActionPerformed

    private void genCollabKeyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genCollabKeyButtonActionPerformed

        String key = generateNewCollaboratorKey ();
        storage.setCollaboratorKey (key);

        collaboratorKeyTextField.setText (key);
    }//GEN-LAST:event_genCollabKeyButtonActionPerformed


    private void runHealthCheckButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runHealthCheckButtonActionPerformed

        // Lambda to log the debugging messages from the health checks
        final Consumer<String> logger = (String msg) -> {

//            log.log (msg);

            Date date = new Date ();
            SimpleDateFormat sdf = new SimpleDateFormat ("[yyyy-MM-dd HH:mm:ss.SSS] ");
            healthCheckTextArea.append (sdf.format (date) + msg + "\n");
        };

        
        try {

            if ( (poller != null) 
                && poller.isRunning ()) {
                
                int selected_option = JOptionPane.showConfirmDialog (
                        this,
                        "There is a polling session already running. Do you "
                        + "want to stop it before running the health check?\n\n"
                        + "If you click 'No', the current running configuration "
                        + "will be used to perform the checks."
                );

                switch (selected_option) {
                    case JOptionPane.YES_OPTION:
                        
                        poller.stop ();
                        healthCheckTextArea.append ("Polling stopped.\n");                    
                        reloadPoller ();
                        break;
                        
                    case JOptionPane.CANCEL_OPTION:

                        healthCheckTextArea.append ("Healthcheck cancelled.\n");
                        return;
                }

            } else {
                
                reloadPoller ();
            }

            /* If the previous step failed, which would imply that the poller
            was not correctly instantiated, an exception wouldn've been thrown.
            Therefore, if it reached this point, it means that a poller exist */
            new Thread ( () -> {

                boolean success = false;
                
                try {
                    success = poller.healthCheck (logger);

                } catch (Exception ex) {

                    log.error (ex);
                    healthCheckTextArea.append ("Exception running the " +
                            "health checks: " + ex.getLocalizedMessage () + "\n"
                    );
                }

                if (success) {

                    healthCheckTextArea.append (
                            "Success!\nThe poller is ready to be used.\n"
                    );

                } else {

                    healthCheckTextArea.append (
                            "Please, verify your configuration.\n"
                    );
                }

            }).start ();

        } catch (IllegalArgumentException ex) {

            log.error (ex);
            healthCheckTextArea.append ("Exception initialising the " +
                    "poller: " + ex.getLocalizedMessage () + "\n"
            );
        }

    }//GEN-LAST:event_runHealthCheckButtonActionPerformed

    private void setAddrButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setAddrButtonActionPerformed

        String addrStr = listenerAddrTextField.getText ();

        try {

            InetAddress addr = InetAddress.getByName (addrStr);
            
            storage.setListenerAddr (addr);
            
            log.log ("New listener address: " + addr);

        } catch (UnknownHostException ex) {

            log.error (ex);
            JOptionPane.showMessageDialog (
                this,
                ex.getLocalizedMessage (),
                "Error!",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_setAddrButtonActionPerformed

    private void setPortButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setPortButtonActionPerformed
        
        Integer newPort = (Integer)listenerPortSpinner.getValue ();

        log.log ("New listener port: " + newPort);

        storage.setListenerPort (newPort);
    }//GEN-LAST:event_setPortButtonActionPerformed

    private void genBundleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genBundleButtonActionPerformed

        JOptionPane.showMessageDialog (this, "Not implemented yet");
        
    }//GEN-LAST:event_genBundleButtonActionPerformed

    private void startPollerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startPollerButtonActionPerformed

        // Lambda to log the debugging messages from the poller
        final Consumer<String> logger = (String msg) -> {

//            log.log (msg);

            Date date = new Date ();
            SimpleDateFormat sdf = new SimpleDateFormat ("[yyyy-MM-dd HH:mm:ss.SSS] ");
            debugPollingTextArea.append (sdf.format (date) + msg + "\n");
        };
        
        // Lambda to print the final event in the UI
        final Consumer<String> eventHandler = (String msg) -> {

            Date date = new Date ();
            SimpleDateFormat sdf = new SimpleDateFormat ("[yyyy-MM-dd HH:mm:ss.SSS] ");
            outputTextArea.append (sdf.format (date) + msg + "\n");

            /* It would be nice to light up the extension tab, but it doesn't
            seem to be currently (as of 2024-01-14) possible with the Montoya API:
                https://forum.portswigger.net/thread/suitab-highlight-when-action-is-performed-in-contextmenu-d342c438
            */
        };

        try {

            reloadPoller ();
            poller.start (logger, eventHandler);
            logger.accept ("Started polling");

        } catch (Exception ex) {

            log.error (ex);
            JOptionPane.showMessageDialog (this, "Exception initialising the " +
                    "poller: " + ex.getLocalizedMessage (),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_startPollerButtonActionPerformed

    private void stopPollerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopPollerButtonActionPerformed

        if (poller != null) {

            poller.stop ();
//            log.log ("Stopped polling");
            debugPollingTextArea.append ("Stopped polling\n");
        }
        
    }//GEN-LAST:event_stopPollerButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField collaboratorKeyTextField;
    private javax.swing.JPanel configTab;
    private javax.swing.JScrollPane debugPollingScrollPane;
    private javax.swing.JTextArea debugPollingTextArea;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler17;
    private javax.swing.Box.Filler filler18;
    private javax.swing.Box.Filler filler19;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler20;
    private javax.swing.Box.Filler filler28;
    private javax.swing.Box.Filler filler29;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler30;
    private javax.swing.Box.Filler filler31;
    private javax.swing.Box.Filler filler32;
    private javax.swing.Box.Filler filler33;
    private javax.swing.Box.Filler filler34;
    private javax.swing.Box.Filler filler35;
    private javax.swing.Box.Filler filler36;
    private javax.swing.Box.Filler filler37;
    private javax.swing.Box.Filler filler38;
    private javax.swing.Box.Filler filler39;
    private javax.swing.Box.Filler filler40;
    private javax.swing.Box.Filler filler41;
    private javax.swing.Box.Filler filler42;
    private javax.swing.Box.Filler filler43;
    private javax.swing.Box.Filler filler44;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JButton genBundleButton;
    private javax.swing.JButton genCollabKeyButton;
    private javax.swing.JScrollPane healthCheckScrollPane;
    private javax.swing.JTextArea healthCheckTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextField listenerAddrTextField;
    private javax.swing.JSpinner listenerPortSpinner;
    private javax.swing.JScrollPane outputScrollPane;
    private javax.swing.JPanel outputTab;
    private javax.swing.JTextArea outputTextArea;
    private javax.swing.JButton runHealthCheckButton;
    private javax.swing.JComboBox<SERVER_TYPE> serverTypeComboBox;
    private javax.swing.JTabbedPane serverTypeTabPane;
    private javax.swing.JButton setAddrButton;
    private javax.swing.JButton setCollabKeyButton;
    private javax.swing.JButton setPortButton;
    private javax.swing.JButton startPollerButton;
    private javax.swing.JButton stopPollerButton;
    // End of variables declaration//GEN-END:variables

    private class ExtensionUnloadHandler implements ExtensionUnloadingHandler {
        @Override
        public void extensionUnloaded () {
            Logger.getLogger ().log ("The extension was unloaded.");

            if (poller != null) {
                poller.stop ();
            }
        }
    }
}
