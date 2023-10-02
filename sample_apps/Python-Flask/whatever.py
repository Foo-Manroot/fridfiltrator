from flask import Flask, request
import logging

app = Flask(__name__)
log = app.logger
log.setLevel (logging.INFO)

@app.route ("/api-or-smth")
def jeeeeeez_maaan ():
    f = request.args.get ("f")

    if f:
        file = "../sample_files/" + f

        try:
            contents = open (file, "r").read ()

            log.info (
                "This simulates some work being done in the background.\n" +
                "The file contents are:\n" +
                "========================" +
                contents +
                "========================"
            )
        except Exception as e:
            log.error (
                "Shit hit the fan, but we don't say anything to the user. " +
                "There's no war in Ba Sing Se ;)"
            )
            log.error (e)


    return "I'm not giving you any output, u dum-dum :P\n"

@app.route ("/")
def hello ():
    return 'Hello, World!<br/><a href="/api-or-smth?f=1">Click here, mate!</a>\n'
