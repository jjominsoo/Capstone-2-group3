package youandme.youandme;


import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

@SpringBootApplication
public class YouAndMeApplication {

	public static void main(String[] args) throws IOException, ParseException {

		SpringApplication.run(YouAndMeApplication.class, args);
	}

}
