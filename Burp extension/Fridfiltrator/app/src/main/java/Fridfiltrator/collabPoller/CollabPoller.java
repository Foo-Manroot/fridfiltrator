package Fridfiltrator.collabPoller;

import Fridfiltrator.Poller;
import Fridfiltrator.helpers.ApiWrapper;
import Fridfiltrator.helpers.Logger;
import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.CollaboratorPayload;
import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.collaborator.InteractionFilter;
import burp.api.montoya.collaborator.SecretKey;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JTextArea;

public class CollabPoller implements Poller {
    
    private String key;
    private CollaboratorClient client;
    
    private Logger log;

    /**
     * Constructor.
     * 
     * @param key 
     *              The base64-encoded Collaborator key used for polling.
     * 
     * @throws IllegalArgumentException
     *              If the provided key is not valid
     */
    public CollabPoller (String key) throws IllegalArgumentException {

        this.key = key;

        SecretKey secretKey = SecretKey.secretKey (key);

        try {
            /* For some reason, the JavaDocs doesn't state that this function
            can throw an exception if the key is not valid */
            this.client = ApiWrapper.getInstance ()
                            .getApis ()
                            .collaborator ()
                            .restoreClient (secretKey);

        } catch (Exception ex) {
            throw new IllegalArgumentException (ex);
        }

        this.log = Logger.getLogger ();
    }


    /**
     * Performs a health check on the poller.
     * 
     * @throws UnknownHostException
     *              If the Collaborator domain couldn't be resolved (aka: no DNS
     *          interaction was possible)
     *
     * @throws InterruptedException
     *              If the thread was interrupted while polling 
     */
    @Override
    public boolean healthCheck (JTextArea logArea) throws UnknownHostException, InterruptedException {

        // Lambda to log to both the extension's logger and the UI's text area
        final Consumer<String> logMsg = (msg) -> {
            
            log.log (msg);

            Date date = new Date ();
            SimpleDateFormat sdf = new SimpleDateFormat ("[yyyy-MM-dd HH:mm:ss.SSS] ");
            logArea.append (sdf.format (date) + msg + "\n");
        };

        logMsg.accept ("-----------------------------");
        logMsg.accept ("Running healthcheck for the Collaborator poller...");
        logMsg.accept ("Address of the server: " + client.server ().address ());

        CollaboratorPayload payload = client.generatePayload ();
        
        /* We can just create a simple DNS request and check if it arrived in
        the next 5 retries (for example) */
        String hostname = payload.toString ();
        InetAddress.getByName (hostname);
        
        logMsg.accept ("DNS interaction successful");

        boolean interactionFound = false;
        int maxRetries = 5;
        List<Interaction> inter = new ArrayList<>();

        logMsg.accept ("Polling the server to get the interaction...");
        while ( ! interactionFound && (maxRetries >= 0)) {

            inter = client.getInteractions (
                InteractionFilter.interactionPayloadFilter (
                    payload.toString ()
                )
            );

            interactionFound = ! inter.isEmpty ();
            maxRetries--;

            if ( ! interactionFound ) {
                logMsg.accept ("Attempt failed. Retrying in 1s...");
                Thread.sleep (1000);
            }
        }

        if (interactionFound) {
            logMsg.accept ("Got interaction!");
            logMsg.accept ("All checks passed :)");
        } else {
            logMsg.accept ("Couldn't get the interaction from the Collaborator server");
        }

        return interactionFound;
    }    
}
