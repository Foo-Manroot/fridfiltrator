package Fridfiltrator;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import java.net.InetAddress;

public class Fridfiltrator implements BurpExtension {

	private MontoyaApi api;

	private Poller poller;

	@Override
	public void initialize (MontoyaApi api)
	{
		this.api = api;
		// set extension name
		api.extension ().setName ("Fridfiltrator");

		poller = new Poller (InetAddress.getLoopbackAddress (), 9999, api);
		poller.start ();

		api.extension ().registerUnloadingHandler(new ExtensionUnloadHandler());
	}


	private class ExtensionUnloadHandler implements ExtensionUnloadingHandler {
		@Override
		public void extensionUnloaded() {
			api.logging ().logToOutput("Extension was unloaded.");
			poller.shutdown ();
		}
	}
}
