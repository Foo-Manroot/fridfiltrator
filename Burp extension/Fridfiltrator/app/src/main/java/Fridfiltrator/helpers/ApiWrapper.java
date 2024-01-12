package Fridfiltrator.helpers;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.burpsuite.BurpSuite;
import burp.api.montoya.collaborator.Collaborator;
import burp.api.montoya.comparer.Comparer;
import burp.api.montoya.decoder.Decoder;
import burp.api.montoya.extension.Extension;
import burp.api.montoya.http.Http;
import burp.api.montoya.intruder.Intruder;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.organizer.Organizer;
import burp.api.montoya.persistence.Persistence;
import burp.api.montoya.proxy.Proxy;
import burp.api.montoya.repeater.Repeater;
import burp.api.montoya.scanner.Scanner;
import burp.api.montoya.scope.Scope;
import burp.api.montoya.sitemap.SiteMap;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.utilities.Utilities;
import burp.api.montoya.websocket.WebSockets;

/**
 * Generic wrapper for the Montoya API
 */
public class ApiWrapper {
    
    private static ApiWrapper instance = null;

    private MontoyaApi api = null;

    /**
     * Private constructor to allow only a single instance of this class, using
     * the `getInstance()` method.
     * 
     * @param persistence 
     *          The Burp API object managing the persistent storage.
     */
    private ApiWrapper () {

        // Dummy implementations to force an exception if the singleton wasn't
        // properly initialised by the root object
        api = new MontoyaApi() {
            @Override
            public BurpSuite burpSuite() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Collaborator collaborator() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Comparer comparer() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Decoder decoder() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Extension extension() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Http http() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Intruder intruder() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Logging logging() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Organizer organizer() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Persistence persistence() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Proxy proxy() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Repeater repeater() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Scanner scanner() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Scope scope() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public SiteMap siteMap() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public UserInterface userInterface() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public Utilities utilities() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public WebSockets websockets() {
                throw new UnsupportedOperationException ("The ApiWrapper object hasn't been properly initialised by the root object of this extension.");
            }
        };
    }
    
    /**
     * Gets the object in charge of accessing the API. If it wasn't created
     * already, it creates a new instance.
     * 
     * @return
     *          The only allowed instance of this class.
     */
    public static ApiWrapper getInstance () {
        
        if (instance == null) {

            instance = new ApiWrapper ();
        }

        return instance;
    }
    
    /**
     * Initialises (or replaces, if it had already been set) the `api` attribute.
     * 
     * This method needs to be called once by the root class of the extension so
     * the other methods can simply call `ApiWrapper.getApi ()` without having
     * to worry about initialisation.
     * 
     * @param api
     *              Burp's main API object.
     */
    public void init (MontoyaApi api) {

        this.api = api;
    }

    /**
     * Returns the main MontoyaApi object associated with this extension.
     * 
     * @return
     *              duuuh
     * 
     * @see MontoyaApi
     */
    public MontoyaApi getApis () {
        
        return api;
    }
}
