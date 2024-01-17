package Fridfiltrator.poller;

import Fridfiltrator.helpers.ApiWrapper;
import Fridfiltrator.helpers.Logger;
import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.CollaboratorPayload;
import burp.api.montoya.collaborator.DnsDetails;
import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.collaborator.InteractionFilter;
import burp.api.montoya.collaborator.InteractionType;
import burp.api.montoya.collaborator.SecretKey;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;

public class CollabPoller extends Poller {

    private CollaboratorClient client;
    
    /**
     * Hashmap for events that weren't completely retrieved yet; for example, if
     * the polling event happened before the implant finished sending out the
     * event.
     * 
     * Key: Event ID
     * Value: 
     */
    private final HashMap<String, ArrayList<String>> incompleteEvents;

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

        if (key == null) {
            throw new IllegalArgumentException ("The collaborator key cannot be null.");
        }

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

        this.incompleteEvents = new HashMap<>();
    }

    @Override
    public boolean healthCheck (Consumer<String> logger) throws UnknownHostException, InterruptedException {
        
        logger.accept ("-----------------------------");
        logger.accept ("Running healthcheck for the Collaborator poller...");
        logger.accept ("Address of the server: " + client.server ().address ());

        CollaboratorPayload payload = client.generatePayload ();
        
        logger.accept ("Payload: " + payload);
        
        /* We can just create a simple DNS request and check if it arrived in
        the next 5 retries (for example) */
        String hostname = payload.toString ();
        InetAddress.getByName (hostname);
        
        logger.accept ("DNS interaction successful");

        boolean interactionFound = false;
        int maxRetries = 5;
        List<Interaction> inter = new ArrayList<>();

        logger.accept ("Polling the server to get the interaction...");
        while ( ! interactionFound && (maxRetries >= 0)) {

            inter = client.getInteractions (
                InteractionFilter.interactionPayloadFilter (
                    payload.toString ()
                )
            );

            interactionFound = ! inter.isEmpty ();
            maxRetries--;

            if ( ! interactionFound ) {
                logger.accept ("Attempt failed. Retrying in 2s...");
                Thread.sleep (2000);
            }
        }

        if (interactionFound) {
            logger.accept ("Got interaction!");
            logger.accept ("All checks passed :)");
        } else {
            logger.accept ("Couldn't get the interaction from the Collaborator server");
        }

        return interactionFound;
    }

    @Override
    protected List<byte []> poll (Consumer<String> logger) {

        ArrayList<byte[]> events = new ArrayList<>();
        
        logger.accept ("Global attribute: " + incompleteEvents);

        List<Interaction> collabInteractions = client.getAllInteractions ();

        collabInteractions.forEach (
            (Interaction i) -> {

                if (i.type ().equals (InteractionType.DNS)) {
                    
                    try {
                        DnsDetails dnsDetails = i.dnsDetails ().orElseThrow ();
                        
                        // This is the full DNS packets, headers and all
                        byte [] packet = dnsDetails.query ().getBytes ();

                        Message dns = new Message (packet);

                        /* The expected query should contain at least 6 tags
                            (or more, depending on the collaborator base payload):

                            <data>.<current_index>.<total_items>.<event_id>.<bas_collaborator_payload>.<root>

                            The collaborator payload can contain 3 (or more?) tags.
                            For example, the public server is:
                                <payload>.oastify.com
                        */
                        Name question = dns.getQuestion ().getName ();
                        
                        logger.accept ("Got DNS request: " + question);

                        if (question.labels () < 6) {
                            throw new ParseException ("The requested DNS name contained less labels than expected", 0);
                        }

                        String data = question.getLabelString (0);

                        // 0-based index
                        int current_index = Integer.parseInt (question.getLabelString (1));
                        int total_items = Integer.parseInt (question.getLabelString (2));

                        if ( (total_items < 0)
                            || (total_items > 100) // Max 100 items per event (completely arbitrary upper bound)
                            ) {
                            throw new ParseException ("N. items out of bounds: " + total_items, 1);
                        }

                        if ( (current_index < 0)
                            || (current_index > 100) // Max 100 items per event (completely arbitrary upper bound)
                            || (current_index >= total_items)
                            ) {
                            throw new ParseException ("Index out of bounds: " + current_index, 2);
                        }

                        logger.accept ("Got data: " + data + "\nIdx: "
                                + current_index + "/" + total_items
                        );

                        String event_id = question.getLabelString (3);

                        /* There might be some leftover events from the
                        previous polling.
                            Otherwise, we allocate a new list to hold all the
                        items, as signaled by the 
                        */
                        ArrayList<String> previous_data = incompleteEvents.getOrDefault (
                                event_id,
                                new ArrayList<>(
                                        Collections.nCopies (total_items, null)
                                )
                        );
                        previous_data.set (current_index, data);

                        /* If the event was fully reassembled, we can rely it to
                        the caller for further processing */
                        if (previous_data.stream ().allMatch (s -> s != null)) {

                            String result = previous_data.stream ().reduce (
                                "",
                                (a, b) -> a.concat (b)
                            );

                            logger.accept ("Recovered full event: " + result);

                            byte [] decoded = Base64.getUrlDecoder ()
                                                    .decode (result);

                            events.add (decoded);
                            incompleteEvents.remove (event_id);
                        } else {
                            /* Otherwise, just put it back on the global HashMap
                            and wait for the next poll */
                            logger.accept ("Some parts are still missing. Current " +
                                "status of the list: " + previous_data
                            );

                            incompleteEvents.put (event_id, previous_data);
                        }

                    } catch (NoSuchElementException ex) {

                        logger.accept ("Tried to get the DNS details, but got " +
                                "nothing: " + ex.getLocalizedMessage ()
                        );
                    } catch (IOException
                            | ParseException
                            | IllegalArgumentException
                            | IllegalStateException ex) {

                        logger.accept ("Failed to parse the DNS raw " +
                                "packet: " + ex.getLocalizedMessage ()
                        );
                    }

                } else {

                    logger.accept (
                            "Unexpected interaction of type: " + i.type () + ":"
                            + "\nOrigin: " + i.clientIp () + ":" + i.clientPort ()
                            + "\nTimestamp: " + i.timeStamp ()
                            + "\nCustom data: " + i.customData ().orElse ("N/A")
                    );
                }
            }
        );
        

        return events;
    }
}
