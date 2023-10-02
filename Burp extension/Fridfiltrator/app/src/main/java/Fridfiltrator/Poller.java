package Fridfiltrator;

import burp.api.montoya.MontoyaApi;
import com.macasaet.fernet.Key;
import com.macasaet.fernet.StringValidator;
import com.macasaet.fernet.Token;
import com.macasaet.fernet.Validator;
import jakarta.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author foo
 */
public class Poller {

	private InetAddress redirector_address;

	private int redirector_port;

	private MontoyaApi api;

	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	private ScheduledFuture<?> schedule;

	private final byte EOD_SIGNAL = 0x00;

//	private Thread udp_listener;

	public Poller (InetAddress redirector_address, int redirector_port, MontoyaApi api) {

		this.redirector_address = redirector_address;
		this.redirector_port = redirector_port;
		this.api = api;
	}

	public void start () {

		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor (1);
		schedule = scheduledThreadPoolExecutor.scheduleAtFixedRate (
				new PollingRunnable(),
				0,
				10,
				TimeUnit.SECONDS
		);

//		udp_listener = new Thread (
//				new UDPServer ()
//		);
//		udp_listener.start ();

		api.logging ().logToOutput ("Started polling every 2 seconds");
	}

	public void shutdown () {

		schedule.cancel (true);
		scheduledThreadPoolExecutor.shutdown ();

//		udp_listener.interrupt ();
	}

	private class PollingRunnable implements Runnable {

		// Hardcoded for testing
		private final Key encryption_key = new Key ("0KET_f5D_YcaSQicPfhEUJG0aIYDps0sFkgcj_BC9fQ=");

		private final Validator<String> validator = new StringValidator () {
			public TemporalAmount getTimeToLive () {
				return Duration.ofDays (999999); // Outrageous validity, for testing
			}
		};


		private int ntohl (byte[] network_data) {


			ByteBuffer buf = ByteBuffer.wrap (network_data);
			buf.order(ByteOrder.BIG_ENDIAN); // Network order

			return buf.getInt ();
		}

		private void processEvent (byte[] data) {
/*			api.logging ().logToOutput (
				"Got event data: "
				+ DatatypeConverter.printHexBinary (data)
			);
*/
			if (data.length < 8) { // Sanity check
				api.logging ().logToError ("Dafuq? I expected at least 8 Bytes, but "
						+ "received: " + DatatypeConverter.printHexBinary (data));
			}

			/* First 4 Bytes: timestamp */
			int timestamp = ntohl (Arrays.copyOfRange (data, 0, 4));

			/* Next 4 bytes: data length */
			int data_len = ntohl (Arrays.copyOfRange (data, 4, 8));

			/* Rest of the array: encrypted data */
			byte[] enc_data = Arrays.copyOfRange (data, 8, data_len + 8);

			try {
				Token fernet_token = Token.fromBytes (enc_data);
				String dec_data = fernet_token.validateAndDecrypt (encryption_key, validator);

				api.logging ().logToOutput (
					"@" + timestamp + " -> " + dec_data
				);
			} catch (Exception ex) {
				api.logging().logToError("Exception: " + ex.getMessage());
			}
		}

		@Override
		public void run () {

			api.logging ().logToOutput ("\n=============\n\nPaul'ing...");

			// First byte: 0x00 (MSG_TYPE.POLLING)
			// 2-4 bytes: timestamp to request events newer than it
			byte[] data = { 0x00, 0x00, 0x00, 0x00, 0x01 };

			DatagramPacket packet = new DatagramPacket (
					data, data.length,
					redirector_address, redirector_port
			);

			try {
				DatagramSocket socket = new DatagramSocket ();

				byte[] buf = new byte[512];
				DatagramPacket response = new DatagramPacket (buf, buf.length);

				// Wait, at most, 2000 milliseconds for a response
				socket.setSoTimeout(2000);

				socket.send (packet);
				while (true) {
					socket.receive (response);

					byte[] resp_data = Arrays.copyOfRange (buf, 0, buf.length);

					if (resp_data[0] == EOD_SIGNAL) {

						api.logging ().logToOutput ("-- No more events available --");
						break;
					}

					processEvent (resp_data);
				}

				socket.close ();

			} catch (SocketException  ex) {

				api.logging ().raiseCriticalEvent (ex.getMessage ());

			} catch (IOException ex) {

				api.logging ().raiseCriticalEvent (ex.getMessage ());
			}
		}
	}
	/*
	private class UDPServer implements Runnable {

		@Override
		public void run() {

			try {
				DatagramSocket socket = new DatagramSocket (6666, InetAddress.getLoopbackAddress ());
				api.logging ().logToOutput ("Waiting for connections on udp://127.0.0.1:6666");

				while (true) {

					byte[] req_data = new byte[512];
					DatagramPacket request = new DatagramPacket (req_data, req_data.length);

					socket.receive (request);

					api.logging ().logToOutput (
							"Got data: "
							+ new String (request.getData ())
					);
				}

			} catch (SocketException ex) {

				api.logging ().raiseCriticalEvent (ex.getMessage ());
			} catch (IOException ex) {

				api.logging ().raiseCriticalEvent (ex.getMessage ());
			}
		}
	}
	*/
}
