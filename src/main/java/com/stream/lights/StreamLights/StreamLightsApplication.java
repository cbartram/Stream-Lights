package com.stream.lights.StreamLights;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
public class StreamLightsApplication {

	@Value("${http.connect.timeout}")
	private int connectTimeout;

	@Value("${http.read.timeout}")
	private int readTimeout;

	public static void main(String[] args) {
		SpringApplication.run(StreamLightsApplication.class, args);
	}

	/**
	 * Constructs a customized RestTemplate for making http calls to external services
	 * @param builder RestTemplateBuilder builder object to create and modify properties
	 *                of the rest template.
	 * @return RestTemplate fully constructed rest template object.
	 */
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
				.setConnectTimeout(Duration.ofMillis(connectTimeout))
				.setReadTimeout(Duration.ofMillis(readTimeout))
				.build();
	}
}
