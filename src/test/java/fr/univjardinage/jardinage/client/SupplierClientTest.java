package fr.univjardinage.jardinage.client;

import tools.jackson.databind.ObjectMapper;
import fr.univjardinage.jardinage.dto.StockAvailabilityDTO;
import fr.univjardinage.jardinage.dto.SupplierProductDTO;
import fr.univjardinage.jardinage.exception.ResourceNotFoundException;
import fr.univjardinage.jardinage.exception.ServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import fr.univjardinage.jardinage.config.RestTemplateConfig;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@DisplayName("Tests du SupplierClient")
@SpringBootTest(
        classes = {
                RestTemplateConfig.class,
                SupplierClient.class
        }
)
@TestPropertySource(properties = {
        "supplier.api.base-url=https://api.fournisseur-jardinage.com/v1",
        "supplier.api.key=default-key"
})

class SupplierClientTest {

    @Autowired
    private SupplierClient supplierClient;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String baseUrl =
            "https://api.fournisseur-jardinage.com/v1";

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer
                .bindTo(restTemplate)
                .bufferContent()
                .build();
    }


    @Test
    @DisplayName("Doit récupérer les produits d'un fournisseur")
    void testGetProducts() throws Exception {

        // Given
        List<SupplierProductDTO> expectedProducts = Arrays.asList(

                SupplierProductDTO.builder()
                        .productCode("P001")
                        .name("Rosier rouge")
                        .price(new BigDecimal("15.99"))
                        .build(),

                SupplierProductDTO.builder()
                        .productCode("P002")
                        .name("Tondeuse")
                        .price(new BigDecimal("299.99"))
                        .build()
        );

        mockServer
                .expect(
                        requestTo(
                                baseUrl
                                        + "/suppliers/SUPP123/products"
                        )
                )
                .andExpect(method(HttpMethod.GET))
                .andExpect(
                        header(
                                "X-API-Key",
                                "default-key"
                        )
                )
                .andRespond(
                        withSuccess(
                                objectMapper.writeValueAsString(
                                        expectedProducts
                                ),
                                MediaType.APPLICATION_JSON
                        )
                );

        // When
        List<SupplierProductDTO> products =
                supplierClient.getProducts("SUPP123");

        // Then
        assertThat(products).hasSize(2);

        assertThat(products.get(0).getProductCode())
                .isEqualTo("P001");

        mockServer.verify();
    }

    @Test
    @DisplayName("Doit vérifier la disponibilité d'un produit")
    void testCheckAvailability() throws Exception {

        // Given
        StockAvailabilityDTO availability =
                StockAvailabilityDTO.builder()
                        .productCode("P001")
                        .available(true)
                        .quantity(50)
                        .deliveryDays(3)
                        .build();

        mockServer
                .expect(
                        requestTo(
                                baseUrl
                                        + "/products/P001/availability"
                        )
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withSuccess(
                                objectMapper.writeValueAsString(
                                        availability
                                ),
                                MediaType.APPLICATION_JSON
                        )
                );

        // When
        StockAvailabilityDTO result =
                supplierClient.checkAvailability("P001");

        // Then
        assertThat(result.getAvailable()).isTrue();

        assertThat(result.getQuantity())
                .isEqualTo(50);

        mockServer.verify();
    }

    @Test
    @DisplayName("Doit gérer l'erreur 404")
    void testHandleNotFound() {

        // Given
        mockServer
                .expect(
                        requestTo(
                                baseUrl
                                        + "/products/INVALID/availability"
                        )
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withStatus(HttpStatus.NOT_FOUND)
                );

        // When / Then
        assertThatThrownBy(
                () -> supplierClient.checkAvailability("INVALID")
        )
                .isInstanceOf(ResourceNotFoundException.class);

        mockServer.verify();
    }

    @Test
    @DisplayName("Doit gérer l'erreur 500")
    void testHandleServerError() {

        // Given
        mockServer
                .expect(
                        requestTo(
                                baseUrl
                                        + "/products/P001/availability"
                        )
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withServerError()
                );

        // When / Then
        assertThatThrownBy(
                () -> supplierClient.checkAvailability("P001")
        )
                .isInstanceOf(ServerException.class);

        mockServer.verify();
    }
}