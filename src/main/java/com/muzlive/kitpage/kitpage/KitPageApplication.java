package com.muzlive.kitpage.kitpage;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class KitPageApplication {

	public static String APPLICATION_LOCATIONS = "spring.config.location="
		+ "classpath:application.yml,";
	public static void main(String[] args) {
		System.out.println("os.name===========" + System.getProperty("os.name"));
		if(System.getProperty("os.name").indexOf("Windows") > -1)
			APPLICATION_LOCATIONS += "optional:E:\\BackUp190425\\workspace_IntelliJ\\S3_config\\Global_SNS\\Tokyo\\application-prod_2.yml";
		else if(System.getProperty("os.name").indexOf("Mac OS X") > -1){
			APPLICATION_LOCATIONS += "optional:/Users/leeseumgin/Documents/source/fuji/application-local.yml";
		}else {
			APPLICATION_LOCATIONS += "optional:/kihno_data/WEB9/application-prod.yml";
		}
		new SpringApplicationBuilder(KitPageApplication.class)
			.properties(APPLICATION_LOCATIONS)
			.run(args);
	}

}
