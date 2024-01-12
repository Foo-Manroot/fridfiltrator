package Fridfiltrator.ui;

import Fridfiltrator.helpers.Storage;
import burp.api.montoya.logging.Logging;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class _TabGUI extends JPanel {

    Logging log;
    Storage storage;
    
    public _TabGUI (Logging log, Storage storage) {

        this.log = log;
        this.storage = storage;
        
        JLabel label = new JLabel ("Delay in ms: ");
        
        int delay = 0;
        
        JTextField delayInput = new JTextField (String.valueOf(delay), 6);
        JButton saveBtn = new JButton ("Save");
        
        saveBtn.addActionListener (
            new ActionListener () {
                @Override
                public void actionPerformed (ActionEvent ae) {

                    String delayText = delayInput.getText ();
                    
                    int delay = Integer.parseInt (delayText);

                   // storage.setDelay (delay);
                    JOptionPane.showMessageDialog (_TabGUI.this, "Delay set to: " + delay);
                }
            }
        );

        this.add (label);
        this.add (delayInput);
        this.add (saveBtn);
        
        log.logToOutput ("UI created");
    }
}
