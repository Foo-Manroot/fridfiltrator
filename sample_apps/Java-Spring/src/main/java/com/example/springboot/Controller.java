package com.example.springboot;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class Controller {

	@GetMapping("/")
	public String index () {

		System.out.println ("Got request tot /");
		return "Hello, World!<br/><a href=\"/api-or-smth?f=1\">Click here, mate!</a>\n";
	}

	@GetMapping("/api-or-smth")
	public String jeeeeeez_maaan (@RequestParam(required = false) String f) {

		System.out.println ("Got request to /api-or-smth");

		if ((f != null) && (f != "")) {

			try {
				Path path = Paths.get ("../sample_files/" + f);
				String contents = String.join ("\n", Files.readAllLines (path));

				System.out.println ("This simulates some work being done in the "
						+ "background.\nThe file contents are:\n"
			            + "========================"
			            + contents
			            + "========================"
					);

			} catch (java.io.IOException e) {

				System.out.println ("[ERROR] Shit hit the fan, but we don't say anything "
						+ " to the user. There's no war in Ba Sing Se ;)"
				);
				System.out.println (e.getMessage ());
			}
		}


		return "I'm not giving you any output, u dum-dum :P\n";
	}
}
