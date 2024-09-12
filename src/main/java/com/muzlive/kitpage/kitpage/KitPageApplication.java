package com.muzlive.kitpage.kitpage;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class KitPageApplication {

	public static void main(String[] args) {

		String APPLICATION_LOCATIONS = "spring.config.location="
			+ "classpath:application.yml,"
			+ "optional:/Users/leeseumgin/Documents/source/kitpage/application-local.yml,"
			+ "optional:/Users/leeseumgin/Documents/source/kitpage/application-dev.yml,"
			+ "optional:/source/application-dev.yml,"
			+ "optional:/source/application-prod.yml";

		new SpringApplicationBuilder(KitPageApplication.class)
			.properties(APPLICATION_LOCATIONS)
			.run(args);
	}

}
