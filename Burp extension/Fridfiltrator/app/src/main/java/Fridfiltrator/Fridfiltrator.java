package Fridfiltrator;

import Fridfiltrator.helpers.ApiWrapper;
import Fridfiltrator.helpers.Logger;
import Fridfiltrator.helpers.Storage;
import Fridfiltrator.ui.UIController;
import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class Fridfiltrator implements BurpExtension {

//    private ListenerPoller poller;

    @Override
    public void initialize (MontoyaApi api)
    {       
        // Initialise the helper singletons
        Storage.getInstance ().init (api.persistence ());
        Logger.getLogger ().init (api.logging ());
        ApiWrapper.getInstance ().init (api);

        // set extension name
        api.extension ().setName ("Fridfiltrator");

        UIController.createUi (api);

//		poller = new ListenerPoller (InetAddress.getLoopbackAddress (), 9999);
//		poller.start ();

//        api.extension ().registerUnloadingHandler (new ExtensionUnloadHandler ());
    }


//    private class ExtensionUnloadHandler implements ExtensionUnloadingHandler {
//        @Override
//        public void extensionUnloaded () {
//            Logger.getLogger ().log ("The extension was unloaded.");
//
//            if (poller != null) {
//                poller.stop ();
//            }
//        }
//    }
}
