package com.danielbukowski.photosharing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PhotoSharingApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoSharingApplication.class, args);
	}

}
