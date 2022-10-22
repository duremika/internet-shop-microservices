package ru.duremika.inventoryservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.duremika.inventoryservice.model.Inventory;
import ru.duremika.inventoryservice.repository.InventoryRepository;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(InventoryRepository repository) {
        return args -> {
            repository.save(
                    new Inventory(null, "iPhone_13", 100)
            );
            repository.save(
                    new Inventory(null, "iPhone_13_red", 0)
            );
        };
    }
}
