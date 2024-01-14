package Fridfiltrator.poller;

import Fridfiltrator.helpers.ApiWrapper;
import burp.api.montoya.MontoyaApi;
import jakarta.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author foo
 */
public class ListenerPoller extends Poller {

    private final InetAddress redirector_address;

    private final int redirector_port;

    private final byte EOD_SIGNAL = 0x00;

    public ListenerPoller (InetAddress redirector_address, int redirector_port) throws IllegalArgumentException {

        if (redirector_address == null) {
            throw new IllegalArgumentException ("The redirector address cannot be null.");
        }

        this.redirector_address = redirector_address;
        this.redirector_port = redirector_port;
    }

    @Override
    public boolean healthCheck (Consumer<String> logger) {

        throw new UnsupportedOperationException ("Some day I will implement this... maybe");
//        return true;
    }

    private int ntohl (byte[] network_data) {

        ByteBuffer buf = ByteBuffer.wrap (network_data);
        buf.order (ByteOrder.BIG_ENDIAN); // Network order

        return buf.getInt ();
    }

    
    @Override
    protected List<byte []> poll (Consumer<String> logger) {
        
        ArrayList<byte[]> events = new ArrayList<>();

        // First byte: 0x00 (MSG_TYPE.POLLING)
        // 2-4 bytes: timestamp to request events newer than it
        byte[] data = { 0x00, 0x00, 0x00, 0x00, 0x01 };

        DatagramPacket packet = new DatagramPacket (
            data, data.length,
            redirector_address, redirector_port
        );

        try (DatagramSocket socket = new DatagramSocket ()) {

            byte[] buf = new byte[512];
            DatagramPacket response = new DatagramPacket (buf, buf.length);

            // Wait, at most, 2000 milliseconds for a response
            socket.setSoTimeout (2000);

            socket.send (packet);
            while (true) {
                socket.receive (response);

                byte[] resp_data = Arrays.copyOfRange (buf, 0, buf.length);

                if (resp_data[0] == EOD_SIGNAL) {

                    logger.accept ("-- No more events available --");
                    break;
                }
                
                if (resp_data.length < 8) { // Sanity check

                    throw new IllegalArgumentException ("Dafuq? I expected at "
                        + "least 8 Bytes, but received: "
                        + DatatypeConverter.printHexBinary (resp_data));
                }

                /* First 4 Bytes: timestamp (expected in BIG_ENDIAN) */
//                int timestamp = ntohl (Arrays.copyOfRange (data, 0, 4));

                /* Next 4 bytes: data length */
                int data_len = ntohl (Arrays.copyOfRange (resp_data, 4, 8));

                /* Rest of the array: encrypted data */
                byte[] enc_data = Arrays.copyOfRange (resp_data, 8, data_len + 8);

                events.add (enc_data);
            }

        } catch (IOException ex) {

                logger.accept (ex.getMessage ());
        }

        return events;
    }
}
