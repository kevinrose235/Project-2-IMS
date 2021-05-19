package com.revature;

import static org.springframework.web.reactive.function.server.RouterFunctions.resources;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class WebConfiguration {

	@Bean
	public RouterFunction<ServerResponse> resRouter() {
		return resources("/**", new ClassPathResource("public/"));
	}
}
