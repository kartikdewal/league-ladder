package dev.kartikdewal.leagueladder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class LeagueLadderApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeagueLadderApplication.class, args);
	}
}
