package com.eliteworks.eliteworks;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.io.*;

@SpringBootApplication
public class EliteworksApplication {

	public static void main(String[] args) throws IOException {
		ClassLoader classLoader = EliteworksApplication.class.getClassLoader();
		InputStream serviceAccount = classLoader.getResourceAsStream("serviceAccountKey.json");

		if (serviceAccount != null) {
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.setDatabaseUrl("https://elite-works-19dd1-default-rtdb.asia-south1.firebasedatabase.app")
					.build();

			FirebaseApp.initializeApp(options);
		} else {
			throw new FileNotFoundException("serviceAccountKey.json not found");
		}

		SpringApplication.run(EliteworksApplication.class, args);
	}

}