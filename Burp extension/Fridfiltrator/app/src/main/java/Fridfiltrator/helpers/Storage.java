package Fridfiltrator.helpers;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.persistence.PersistedObject;
import burp.api.montoya.persistence.Persistence;
import burp.api.montoya.persistence.Preferences;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Storage {
    
    
    public enum SERVER_TYPE {
        COLLABORATOR ("Collaborator"),
        SELF_HOSTED ("Self-Hosted");

        /* Friendly name to show on the UI and the logs */
        private final String friendlyName;
        
        private SERVER_TYPE (String friendlyName) {
            this.friendlyName = friendlyName;
        }

        /**
         * Friendly name of the enum, to be shown on the UI to the user.
         * It's not guaranteed to be unique across values.
         * 
         * @return 
         *          The value's human-friendly name
         */
        public String friendlyName () {
            return friendlyName;
        }
        
        @Override
        public String toString () {
            return friendlyName ();
        }
    };
    

    private static Storage instance = null;

    private static final String _KEY_PREFIX = "fridfiltrator_";

    private static final String K_COLLABORATOR_KEY   = _KEY_PREFIX + "COLLABORATOR_KEY";
    private static final String K_LISTENER_ADDR        = _KEY_PREFIX + "LISTENER_IP";
    private static final String K_LISTENER_PORT      = _KEY_PREFIX + "LISTENER_PORT";
    private static final String K_SERVER_TYPE        = _KEY_PREFIX + "SERVER_TYPE";

    private Persistence persistence;

    /**
     * Private constructor to allow only a single instance of this class, using
     * the `getInstance()` method.
     * 
     * @param persistence 
     *          The Burp API object managing the persistent storage.
     */
    private Storage () {

        // Dummy implementations to force an exception if the singleton wasn't
        // properly initialised by the root object
        persistence = new Persistence () {
            @Override
            public PersistedObject extensionData () {
                throw new UnsupportedOperationException ("The Storage object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Preferences preferences () {
                throw new UnsupportedOperationException ("The Storage object hasn't been properly initialised by the root object of this extension.");
            }
        };
    }
    
    /**
     * Gets the object in charge of managing the persistent storage. If it
     * wasn't created already, it creates a new instance.
     * 
     * @return
     *          The only allowed instance of this class.
     */
    public static Storage getInstance () {
        
        if (instance == null) {

            instance = new Storage ();
        }

        return instance;
    }
    
    /**
     * Initialises (or replaces, if they've already been set) the `persistence`
     * and `log` attributes.
     * 
     * This method needs to be called once by the root class of the extension so
     * the other methods can simply call `Storage.getInstance ()` without having
     * to worry about Burp's API.
     * 
     * @param persistence
     *              Burp's persistence storage where to save the data.
     */
    public void init (Persistence persistence) {
        
        this.persistence = persistence;
    }

    /* --------------- Storage setters and getters --------------- */
    
    /**
     * Saves the value of the Collaborator key into the persistent storage.
     * It's always assumed to be successful.
     * 
     * @param newVal 
     *      The new value to be stored in place of the previous one. It's
     *      expected to be a base64-encoded string, but no check is performed.
     */
    public void setCollaboratorKey (String newVal) {

        this.persistence.extensionData ().setString (
                K_COLLABORATOR_KEY,
                newVal
        );
    }
    
    /**
     * Retrieves the saved Collaborator key (if any) from the persistent storage.
     * If the key hasn't been yet saved, this method returns null.
     * 
     * @return
     *      The Collaborator key, as a (maybe? - depends on what value did
     *      `setCollaboratorKey()` receive...) base64-encoded string, or null if
     *      the key wasn't found in the storage.
     */
    public String getCollaboratorKey () {
        
        String retVal = this.persistence
                            .extensionData ()
                            .getString (K_COLLABORATOR_KEY);

        return retVal;
    }

    
    /**
     * Saves the value of the Listener IP address into the persistent storage.
     * It's always assumed to be successful.
     * 
     * @param newVal 
     *      The new value to be stored in place of the previous one.
     */
    public void setListenerAddr (InetAddress newVal) {

        ByteArray ba = ByteArray.byteArray (newVal.getAddress ());

        this.persistence.extensionData ().setByteArray (
                K_LISTENER_ADDR,
                ba
        );
    }
    
    /**
     * Retrieves the saved Listener IP address (if any) from the persistent
     * storage. If no value has been yet saved, this method returns null.
     * 
     * @return
     *      The listener IP (v4 or v6, depends on what `setListenerIP()`
     *      received) address, or null if the key wasn't found in the storage;
     *      or it wasn't a valid IP (v4 or v6) address.
     */
    public InetAddress getListenerAddr () {

        ByteArray ba = this.persistence
                            .extensionData ()
                            .getByteArray (K_LISTENER_ADDR);

        InetAddress retVal = null;

        try {
            if (ba != null) {
                retVal = InetAddress.getByAddress (
                    ba.getBytes ()
                );
            }
        } catch (UnknownHostException ex) {

            Logger.getLogger ().error (ex);
        }

        return retVal;
    }
    
    /**
     * Saves the value of the Listener port into the persistent storage.
     * It's always assumed to be successful.
     * 
     * @param newVal 
     *      The new value to be stored in place of the previous one. It's
     *      expected to be a valid port number (1->65.535), but no check is
     *      performed.
     */
    public void setListenerPort (int newVal) {

        this.persistence.extensionData ().setInteger (
                K_LISTENER_PORT,
                newVal
        );
    }
    
    /**
     * Retrieves the saved Listener port (if any) from the persistent storage.
     * If the key hasn't been yet saved or contained a non-valid port number,
     * this method returns 1.
     *
     * @return
     *      The Listener port, or 1 if the key was either not found or an
     *      invalid port number (not within the range 1->65.535).
     */
    public int getListenerPort () {
        
        Integer retVal = this.persistence
                            .extensionData ()
                            .getInteger (K_LISTENER_PORT);
        
        if ( (retVal == null)
            || (retVal < 1)
            || (retVal > 65535)) {

            retVal = 1;
        }

        return retVal;
    }
    
    
    /**
     * Saves the value of the selected server type.
     * 
     * @param newVal 
     *      The new value to be stored in place of the previous one.
     */
    public void setServerType (SERVER_TYPE newVal) {

        this.persistence.extensionData ().setString (
                K_SERVER_TYPE,
                newVal.name ()
        );
    }

    /**
     * Retrieves from the persistent storage the server type that was selected
     * for the last time (if any).
     * If the key hasn't been yet saved, this method returns
     * {@link SERVER_TYPE#COLLABORATOR} as the default one.
     * 
     * @return
     *      The selected server type.
     */
    public SERVER_TYPE getServerType () {

        String stored = this.persistence
                                .extensionData ()
                                .getString (K_SERVER_TYPE);
        
        SERVER_TYPE retVal = (stored == null)?
                SERVER_TYPE.COLLABORATOR
                :
                SERVER_TYPE.valueOf (stored)
        ;


        return retVal;
    }
    
}
