package com.example.demo;

import com.example.demo.models.PlantInventoryEntry;
import com.example.demo.models.valueobject.Money;
import com.example.demo.repositories.PlantInventoryEntryRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context =SpringApplication.run(DemoApplication.class, args);

/*		PlantInventoryEntry entry = new PlantInventoryEntry();
		entry.setName("Bike");
		entry.setDescription("Nice and shiny");
		entry.setPrice(Money.of(new BigDecimal(100)));


		PlantInventoryEntryRepository repo = context.getBean(PlantInventoryEntryRepository.class);
		repo.save(entry);


		entry = new PlantInventoryEntry();
		entry.setName("Truck");
		entry.setDescription("A bit rusty");
		entry.setPrice(Money.of(new BigDecimal(100)));

		repo.save(entry);

		// % sign is because SQl wild card
		System.out.println(repo.findByNameLike("B%"));

		System.out.println(repo.findByNameContaining("ik"));*/


//		System.out.println(repo.findMethod("ik"));




	}



}
