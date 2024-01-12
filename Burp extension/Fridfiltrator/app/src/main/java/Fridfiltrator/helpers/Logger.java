package Fridfiltrator.helpers;

import burp.api.montoya.logging.Logging;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static Logger instance = null;

    private Logging log;

    /**
     * Private constructor to allow only a single instance of this class, using
     * the `getLogger()` method.
     */
    private Logger () {

        // Dummy implementations to force an exception if the singleton wasn't
        // properly initialised by the root object
        log = new Logging () {
            @Deprecated
            @Override
            public PrintStream output () {
                throw new UnsupportedOperationException ("The Logger object hasn't been properly initialised by the root object of this extension.");
            }

            @Deprecated
            @Override
            public PrintStream error () {
                throw new UnsupportedOperationException ("The Logger object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public void logToOutput (String message) {
                throw new UnsupportedOperationException ("The Logger object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public void logToError (String message) {
                throw new UnsupportedOperationException ("The Logger object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public void logToError (String message, Throwable cause) {
                throw new UnsupportedOperationException ("The Logger object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public void logToError (Throwable cause) {
                throw new UnsupportedOperationException ("The Logger object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public void raiseDebugEvent (String message) {
                throw new UnsupportedOperationException ("The Logger object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public void raiseInfoEvent (String message) {
                throw new UnsupportedOperationException ("The Logger object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public void raiseErrorEvent (String message) {
                throw new UnsupportedOperationException ("The Logger object hasn't been properly initialised by the root object of this extension.");
            }

            @Override
            public void raiseCriticalEvent (String message) {
                throw new UnsupportedOperationException ("The Logger object hasn't been properly initialised by the root object of this extension.");
            }
        };
    }
    
    /**
     * Gets the object in charge of managing the logging in Burp. If it
     * wasn't created already, it creates a new instance.
     * 
     * @return
     *          The only allowed instance of this class.
     */
    public static Logger getLogger () {
        
        if (instance == null) {

            instance = new Logger ();
        }

        return instance;
    }

    /**
     * Initialises (or replaces, if it has already been set) the `log` attribute.
     * 
     * This method needs to be called once by the root class of the extension so
     * the other methods can simply call `Logger.getLogger ()` without having
     * to worry about Burp's API.
     * 
     * @param log 
     *              Burp's logging
     */
    public void init (Logging log) {
        
        this.log = log;
    }
    
    /**
     * Generates a prefix for the log entries
     */
    private String getPrefix () {
        
        Date date = new Date ();
        SimpleDateFormat sdf = new SimpleDateFormat ("[yyyy-MM-dd HH:mm:ss.SSS] ");
        
        return sdf.format (date);
    }
    
    /**
     * Prints a message on the extension's standard output.
     * 
     * @param message 
     *              The message to print.
     */
    public void log (String message) {
        
        log.logToOutput (getPrefix () + message);
    }
    
    /**
     * Prints a message on the extension's standard error.
     * 
     * @param message 
     *              The message to print.
     */
    public void error (String message) {
        
        log.logToError (getPrefix () + message);
    }
 
    /* Apparently, calling the following methods (which are clearly documented
        in the Javadocs ( https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/logging/Logging.html#logToError(java.lang.Throwable) )
        throws the following exception:
            java.lang.NoSuchMethodError: 'void burp.api.montoya.logging.Logging.logToError(java.lang.Throwable)'

        WTF?
    */
//    /**
//     * Prints a stack trace to the current extension's standard error stream.
//     * 
//     * @param cause
//     *              The cause of the error being logged.
//     */
//    public void error (Throwable cause) {
//        
//        log.logToError (cause);
//    }
//    
//    /**
//     * Prints a message and a stack trace to the current extension's standard
//     * error stream.
//     * 
//     * @param message
//     *              The message to print.
//     * 
//     * @param cause
//     *              The cause of the error being logged.
//     */
//    public void error (String message, Throwable cause) {
//
//        log.logToError (getPrefix () + message, cause);
//    }
}
