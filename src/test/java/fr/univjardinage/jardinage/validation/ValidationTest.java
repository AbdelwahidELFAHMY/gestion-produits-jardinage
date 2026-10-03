package fr.univjardinage.jardinage.validation;

import fr.univjardinage.jardinage.dto.CreateProductDTO;
import fr.univjardinage.jardinage.entity.ProductCategory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Tests de validation des DTOs")
class ValidationTest {

    @Autowired
    private Validator validator;

    @BeforeEach
    void setUpLocale() {
        Locale.setDefault(Locale.FRENCH);
    }

    @Test
    @DisplayName("DTO valide ne doit avoir aucune violation")
    void testValidDTO() {

        // Given
        CreateProductDTO dto = CreateProductDTO.builder()
                .name("Rosier")
                .category(ProductCategory.PLANTE)
                .price(new BigDecimal("15.99"))
                .stock(50)
                .description("Belle plante")
                .build();

        // When
        Set<ConstraintViolation<CreateProductDTO>> violations =
                validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Nom vide doit generer une violation")
    void testBlankName() {

        // Given
        CreateProductDTO dto = CreateProductDTO.builder()
                .name(" ")
                .category(ProductCategory.PLANTE)
                .price(new BigDecimal("15.99"))
                .stock(50)
                .build();

        // When
        Set<ConstraintViolation<CreateProductDTO>> violations =
                validator.validate(dto);

        // Then
        assertThat(violations).hasSize(2);

        assertThat(violations)
                .anyMatch(v ->
                        v.getMessage().contains("nom du produit est obligatoire"));
    }

    @Test
    @DisplayName("Prix negatif doit generer une violation")
    void testNegativePrice() {

        // Given
        CreateProductDTO dto = CreateProductDTO.builder()
                .name("Produit")
                .category(ProductCategory.PLANTE)
                .price(new BigDecimal("-10.00"))
                .stock(50)
                .build();

        // When
        Set<ConstraintViolation<CreateProductDTO>> violations =
                validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Stock negatif doit generer une violation")
    void testNegativeStock() {

        // Given
        CreateProductDTO dto = CreateProductDTO.builder()
                .name("Produit")
                .category(ProductCategory.PLANTE)
                .price(new BigDecimal("15.99"))
                .stock(-5)
                .build();

        // When
        Set<ConstraintViolation<CreateProductDTO>> violations =
                validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
    }
}