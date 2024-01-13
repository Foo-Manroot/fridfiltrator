from abc import ABC, abstractmethod

class Sender (ABC):

    @abstractmethod
    def send_data (self, encrypted_b64):
        """
        Sends the data to the appropriate redirector. This works in a best-effort basis;
        meaning, it doesn't (and probably can't even if it wanted to) verify the message
        arrived at the destination.

        This method doesn't return anything.

        @param enc_b64: string
                        Base64-encoded and encrypted payload created by Frida.
        """
        pass

