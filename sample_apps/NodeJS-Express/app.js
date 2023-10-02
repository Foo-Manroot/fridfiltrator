const express = require("express");
const fs = require("fs");

const app = express();
const hostname = "127.0.0.1";
const port = 1122;

app.get("/", (req, res) => {
    res.send('Hello, World!<br/><a href="/api-or-smth?f=1">Click here, mate!</a>\n');
});


app.get("/api-or-smth", (req, res) => {

	const file = req.query.f;

	if (file) {

		fs.readFile("sample_files/" + file, "utf8", (err, data) => {

			if (err) {
				console.error("Shit hit the fan, but we don't say anything to the " +
					"user. There's no war in Ba Sing Se ;)");
				console.error(err);
				return;
			}

			console.log ("This simulates some work being done in the background.\n" +
				"The file contents are:\n" +
				"========================" +
				data +
	            "========================"
			);

		});

	}

	res.send("I'm not giving you any output, u dum-dum :P\n");
});

app.listen(port, () => {
    console.log(`Server running at http://${hostname}:${port}/`);
});
