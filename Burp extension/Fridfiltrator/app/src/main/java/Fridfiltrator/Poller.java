package Fridfiltrator;

import javax.swing.JTextArea;

public interface Poller {

    // We can throw exceptions to signal errors generated during the check, like
    // a bad internet connection or whatever
    //
    // The logging JTextArea is supposed to be used to log the checks progress
    public boolean healthCheck (JTextArea logArea) throws Exception;
}
