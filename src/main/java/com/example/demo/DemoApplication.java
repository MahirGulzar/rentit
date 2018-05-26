package com.example.demo;

import com.example.demo.mailing.TestEmailServer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class DemoApplication {
	@Configuration
	static class ObjectMapperCustomizer {
		@Autowired
		@Qualifier("_halObjectMapper")
		private ObjectMapper springHateoasObjectMapper;


		@Bean(name = "objectMapper")
		ObjectMapper objectMapper() {
			return springHateoasObjectMapper
					.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
					.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
					.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
					.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
					.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
					.registerModules(new JavaTimeModule());
		}
		@Bean
		public RestTemplate restTemplate() {
			RestTemplate _restTemplate = new RestTemplate();
			List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
			messageConverters.add(new MappingJackson2HttpMessageConverter(springHateoasObjectMapper));
			_restTemplate.setMessageConverters(messageConverters);
			return _restTemplate;
		}
	}


	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);

		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		TestEmailServer mailServer = new TestEmailServer();
		exec.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				mailServer.check();
			}
		}, 0, 30, TimeUnit.SECONDS);
	}




}
