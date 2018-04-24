package com.example.demo;

import com.example.demo.common.domain.model.Money;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import java.math.BigDecimal;

@SpringBootApplication
//@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class DemoApplication {
//
//	@Configuration
//	static class ObjectMapperCustomizer {
//		@Autowired @Qualifier("_halObjectMapper")
//		private ObjectMapper springHateoasObjectMapper;
//
//		@Bean(name = "objectMapper")
//		ObjectMapper objectMapper() {
//			return springHateoasObjectMapper
//					.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//					.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
//					.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
//					.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
//					.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
//					.registerModules(new JavaTimeModule());
//		}
//	}
//
//	public static void main(String[] args) {
//		ConfigurableApplicationContext context =SpringApplication.run(DemoApplication.class, args);
//
////		PlantInventoryEntry entry = new PlantInventoryEntry();
////		entry.setName("Bike");
////		entry.setDescription("Nice and shiny");
////		entry.setPrice(Money.of(new BigDecimal(100)));
////
////
////		PlantInventoryEntryRepository repo = context.getBean(PlantInventoryEntryRepository.class);
////		repo.save(entry);
////
////
////		entry = new PlantInventoryEntry();
////		entry.setName("Truck");
////		entry.setDescription("A bit rusty");
////		entry.setPrice(Money.of(new BigDecimal(100)));
////
////		repo.save(entry);
//
//		// % sign is because SQl wild card
////		System.out.println(repo.findByNameLike("B%"));
////
////		System.out.println(repo.findByNameContaining("ik"));
//
//
//
//
//
//
//
//	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}



}
