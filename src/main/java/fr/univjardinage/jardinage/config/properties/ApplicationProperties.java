package fr.univjardinage.jardinage.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
@Validated
public class ApplicationProperties{

    private Business business = new Business();
    private Stock stock = new Stock();
    private Pricing pricing = new Pricing();

    @Data
    public static class Business{
        @NotBlank(message = " Le nom de l’entreprise est obligatoire")
        private String name = "Jardinage Pro";

        @Email(message = " Email invalide ")
        private String contactEmail = " con tact@jar dinage.fr ";

        @Pattern(regexp = "\\d{10}", message = " Le telephone doit contenir 10 chiffres ")
        private String phone = "0123456789";

        private String address = " 123 Rue des Jardins, 75000 Paris ";
    }

    @Data
    public static class Stock{
        @Min(value = 1, message = " Le seuil minimum doit etre au moins 1")
        @Max(value = 100, message = " Le seuil maximum ne peut pas depasser 100 ")
        private Integer lowStockThreshold = 10;

        @Min(value = 1, message = " Le delai de reappro doit etre au moins 1 jour ")
        private Integer reorderDelayDays = 7;

        private Boolean autoReorder = false;
    }


    @Data
    public static class Pricing{
        @DecimalMin(value = "0.0", message = " La TVA ne peut pas etre negative ")
        @DecimalMax(value = "100.0", message = " La TVA ne peut pas depasser 100% ")
        private BigDecimal defaultTaxRate = new BigDecimal("20.0");

        @DecimalMin(value = "0.0", message = " La remise maximale ne peutpas etre negative ")
        @DecimalMax(value = "50.0", message = " La remise maximale nepeut pas depasser 50% ")
        private BigDecimal maxDiscountRate = new BigDecimal("30.0");

        private String currency = " EUR ";




    }
}