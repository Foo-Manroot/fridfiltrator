package main

import (
	"errors"
	"log"
	"io"
	"net/http"
	"os"
)

func getRoot (w http.ResponseWriter, r *http.Request) {

	log.Println ("got / Request")
	io.WriteString (w,
		"Hello, World!<br/><a href=\"/api-or-smth?f=1\">Click here, mate!</a>\n",
	)
}

func jeeeeeez_maaan (w http.ResponseWriter, r *http.Request) {

	log.Println ("got /api-or-smth Request")

	if r.URL.Query().Has ("f") {
		file := r.URL.Query().Get ("f")

		content, err := os.ReadFile ("../sample_files/" + file)

		if err != nil {
			log.Println ("Shit hit the fan, but we don't say anything to the user. " +
	            "There's no war in Ba Sing Se ;)")
			log.Println (err)

		} else {
			log.Println ("This simulates some work being done in the background.\n" +
		            "The file contents are:\n" +
		            "========================" +
					string (content) +
		            "========================")
		}
	}

	io.WriteString (w, "I'm not giving you any output, u dum-dum :P\n")
}


func main () {
	http.HandleFunc ("/", getRoot)
	http.HandleFunc ("/api-or-smth", jeeeeeez_maaan)

	log.Println ("Starting server on 127.1:1234")
	err := http.ListenAndServe ("127.0.0.1:1234", nil)

	if errors.Is(err, http.ErrServerClosed) {

		log.Println ("server closed")

	} else if err != nil {

		log.Println ("error starting server: ")
		log.Println (err)
		os.Exit (1)
	}
}
