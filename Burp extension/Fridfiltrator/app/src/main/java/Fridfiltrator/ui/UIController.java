package Fridfiltrator.ui;

import burp.api.montoya.MontoyaApi;
import java.awt.Component;


public class UIController {
    
    public static void createUi (MontoyaApi api) {

        Component mainPane = new TabGUI ();

        api.userInterface ().registerSuiteTab ("Fridfiltrator", mainPane);
    }
}
