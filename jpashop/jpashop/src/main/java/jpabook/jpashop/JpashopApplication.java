package jpabook.jpashop;


import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

@SpringBootApplication
public class JpashopApplication {

	public String what;
	public static void main(String[] args) throws IOException, ParseException {



		Reader reader = new FileReader("C:\\Users\\user\\Desktop\\java-spring\\jpashop(temp)\\jpashop\\src\\main\\resources\\json\\test.json");
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

		JSONArray jsonArray = new JSONArray();
		jsonArray.add(jsonObject);

		String jsonStr = jsonArray.toJSONString();
		System.out.println("jsonStr = " + jsonStr);

//		if(jsonArray.size() > 0){
//			for(int i = 0 ; i < jsonArray.size() ; i++){
//				JSONObject object = (JSONObject) jsonArray.get(i);
//				System.out.println((String)object.get("name"));
//			}
//		}
		jsonObject.get("school");


		reader.close();

		SpringApplication.run(JpashopApplication.class, args);
	}

}
