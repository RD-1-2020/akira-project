package org.azurecloud.solutions.akiraclient;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AkiraClientApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(AkiraClientApplication.class)
				.web(WebApplicationType.NONE)
				.run(args);
	}

}
