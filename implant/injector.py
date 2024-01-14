#!/usr/bin/env python3

################
# Last update: 2023-09-24
# Contributors:
#		Foo-Manroot
################

from cryptography.fernet import Fernet
import frida
import argparse
import sys
import logging as log

from senders.self_hosted import SelfHosted
from senders.collaborator import Collaborator

ENCRYPTOR = None
# Token to identify project
IMPLANT_ID = "<NOT-IMPLEMENTED>"
# Redirector where to send the event
#redirector = SelfHosted ("127.0.0.1", 9999)
redirector = Collaborator ("110fmqrhgt935ybeqp8lpapy4pafy4.oastify.com") # Key: Byfl30HnsAxxYtBO7V7Un5sr6Faac5GWTE3FpZR5vkE=

HELP_MSG = """
Spawns a process with an injected Frida engine.
Or, in case the `--pid` switch is specified, Frida is injected into an already running process.

Then, whenever an instrumented function or system call are executed, the event is sent to the remote endpoint, where the pentester will receive it, aiding them in their testing.
"""

log.basicConfig (
    format = "[%(asctime)s] | %(levelname)s | %(funcName)s:%(lineno)d => %(message)s",
    level = log.DEBUG
)

def parse_args ():
    """
    Parses the command line arguments and returns the resulting argparser object:
    https://docs.python.org/3/library/argparse.html
    """
    global ENCRYPTOR
    args = {}

    def type_b64key (s):
        charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_="
        res = True
        f = lambda res, x: res and (x in charset)
        [ res := f(res, x) for x in s ] # fold into res

        if not ((len (s) == 44) and res):
            raise argparse.ArgumentTypeError (f"The provided key '{s}' does not have "
                    + "the proper format.\n\nCheck out https://cryptography.io/en/latest"
                    + "/fernet/ for more info about the encryption scheme used.")
        # The string had a valid format
        return s



    parser = argparse.ArgumentParser (
        description = HELP_MSG,
        formatter_class=argparse.RawDescriptionHelpFormatter # To show the help message as written above, instead of a single line
    )

    parser.add_argument ("-k", "--key",
                         help = "Key to encrypt the data sent between the agent and the tester's Burp extension. This program doesn't check any property and simply assumes it was securely generated. It does check, however, the format (a 44 characters, url-safe, base64-encoded string - see https://cryptography.io/en/latest/fernet/ )",
                         type = type_b64key,
#                         required = True
    )

    parser.add_argument ("-s", "--script",
                         help = "Script to inject on the Frida engine (default: %(default)s)",
                         type = argparse.FileType ("r"), # This directly opens the file when parsing the arguments
                         default = "./injected-script.js"
    )

    parser.add_argument ("-p", "--pid",
                         help = "PID of the process to attach to, instead of spawning a new one",
                         type = int
    )

    args = parser.parse_args ()

    with args.script as file: # This will read and close the opened file
        args.script = file.read ()

    key = args.key
    if key is None:
        print ("No key supplied. Generating a new one...")
#        key = Fernet.generate_key ()
        key = b"0KET_f5D_YcaSQicPfhEUJG0aIYDps0sFkgcj_BC9fQ=" # Hardcoded for testing
        print (f"New key: {key}")

    ENCRYPTOR = Fernet (key)

    return args


def spawn_process (device, process_name, args):
    """
    Spawns the designated process and returns a Frida Session"

    Args:
        device:
    """
    pass


def attach_to_pid (pid, device = frida.get_local_device ()):
    """
    Finds the process running with the specified PID and attaches the debugger.

    Args:
        pid: int
            ID of the target process

        device: DeviceManager
            The device to use for the connection. By default, it's the local device

    Returns: frida.Session
        The session object to interact with the debugged process
    """
    session = None

    try:
        session = device.attach (pid)

    except frida.ProcessNotFoundError as e:
        log.critical (f"Process with PID {pid} not found. Does the process even exist?"
            + "If so, check whether you have the appropriate privileges and "
            + f"OS capabilities // {e}")
        return None

    except frida.PermissionDeniedError as e:
        log.critical (f"Permission denied when trying to attach to process {pid}. Check "
            + f"whether you have the appropriate privileges and OS capabilities // {e}")
        return None

    except Exception as e:
        log.critical (f"Error trying to attach to PID {pid}: {e}")
        return None

    return session



def on_message (message, data):
    """
    Action to perform when a message is received from the injected script

    ATTENTION !!!
    These messages are processed synchronously. That means, if the task takes 50 seconds
    to finish, the injected Frida engine will also block for 50 seconds.
    """
    global ENCRYPTOR
    log.debug (f"message={message} // data={data}")
    payload = message ["payload"]

    if message ["type"] == "error":

        log.error (f"Error from Frida: {message ['description']}")

    elif message ["type"] == "send":

        log.info (f"Frida sent: '{payload}'")

    else:
        log.info (f"Unknown message received: {message}")


    enc_msg = ENCRYPTOR.encrypt (payload.encode ("utf-8"))
    log.debug (f"Encrypted message: {enc_msg}")


    # It's more efficient to send the raw bytes, to avoid sending multiple UDP packets
#    decoded = base64.urlsafe_b64decode (enc_msg)

#    sock = socket.socket (socket.AF_INET, socket.SOCK_DGRAM)
#    sock.sendto (IMPLANT_EVENT + decoded, REDIRECTOR)
#    log.info (f"Data sent to udp://{REDIRECTOR[0]}:{REDIRECTOR[1]}")
    redirector.send_data (enc_msg)



if __name__ == "__main__":
    args = parse_args ()

    session = None
    pid = args.pid

    # If the PID has an invalid format (for example, -1 or a non-existant process), the
    # OS will let us know we're idiots, so no further checking is needed
    if not pid:
        # Spawns a new process, as requested
        log.critical (f"!!! PROCESS SPAWNING NOT IMPLEMENTED YET !!!")
        sys.exit (1)

#        try:
#            pid = device.spawn (args.binary)
#        except Exception as e:
#            log.critical (f"Failed to spawn process: {e}")
#            sys.exit (1)

    else:
        session = attach_to_pid (pid)

        if not session:
            log.critical ("No debugging session could be created. Exiting now.")
            sys.exit (1)

        log.info (f"Attached to process {pid}")


    script = session.create_script (args.script)
    script.on ("message", on_message)
    script.load ()
    input ("\n====\n"
           + "The script was successfully injected.\n"
           + "Press ENTER to finish the debugging session...\n"
           + "====\n")
