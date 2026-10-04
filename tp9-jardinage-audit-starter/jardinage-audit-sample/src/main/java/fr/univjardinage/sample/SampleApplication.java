package fr.univjardinage.sample;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Bean
    CommandLineRunner demonstrateAudit(ProductService productService) {
        return args -> productService.createProduct(1L, "Rosier rouge");
    }
}
