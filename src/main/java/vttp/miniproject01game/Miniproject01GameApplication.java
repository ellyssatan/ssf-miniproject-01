package vttp.miniproject01game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class Miniproject01GameApplication {

	public static void main(String[] args) {
		SpringApplication.run(Miniproject01GameApplication.class, args);
	}

}
