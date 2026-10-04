package fr.univjardinage.jardinage.client;

import java.util.List;

import fr.univjardinage.jardinage.dto.StockAvailabilityDTO;
import fr.univjardinage.jardinage.dto.SupplierProductDTO;
import fr.univjardinage.jardinage.dto.OrderRequest;
import fr.univjardinage.jardinage.dto.OrderResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class SupplierClient {

    private final RestTemplate restTemplate;

    @Value("${supplier.api.base-url}")
    private String baseUrl;

    @Value("${supplier.api.key}")
    private String apiKey;

    /**
     * GET - Récupère la liste des produits d'un fournisseur.
     */
    public List<SupplierProductDTO> getProducts(String supplierId) {

        log.info(
                "Récupération des produits du fournisseur : {}",
                supplierId
        );

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/suppliers/{supplierId}/products")
                .buildAndExpand(supplierId)
                .toUriString();

        HttpHeaders headers = createHeaders();

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<SupplierProductDTO>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<List<SupplierProductDTO>>() {
                        }
                );

        return response.getBody();
    }

    /**
     * GET - Vérifie la disponibilité d'un produit.
     */
    public StockAvailabilityDTO checkAvailability(String productCode) {

        log.info(
                "Vérification de disponibilité pour : {}",
                productCode
        );

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/products/{productCode}/availability")
                .buildAndExpand(productCode)
                .toUriString();

        HttpHeaders headers = createHeaders();

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<StockAvailabilityDTO> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        StockAvailabilityDTO.class
                );

        return response.getBody();
    }

    /**
     * POST - Crée une commande chez le fournisseur.
     */
    public String createOrder(OrderRequest orderRequest) {

        log.info(
                "Création d'une commande : {}",
                orderRequest
        );

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/orders")
                .toUriString();

        HttpHeaders headers = createHeaders();

        HttpEntity<OrderRequest> entity =
                new HttpEntity<>(orderRequest, headers);

        ResponseEntity<OrderResponse> response =
                restTemplate.postForEntity(
                        url,
                        entity,
                        OrderResponse.class
                );

        if (response.getStatusCode() == HttpStatus.CREATED) {

            log.info(
                    "Commande créée avec succès : {}",
                    response.getBody().getOrderId()
            );

            return response.getBody().getOrderId();
        }

        throw new RuntimeException(
                "Échec de création de commande"
        );
    }

    /**
     * PUT - Met à jour une commande existante.
     */
    public void updateOrder(
            String orderId,
            OrderRequest orderRequest
    ) {

        log.info(
                "Mise à jour de la commande : {}",
                orderId
        );

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/orders/{orderId}")
                .buildAndExpand(orderId)
                .toUriString();

        HttpHeaders headers = createHeaders();

        HttpEntity<OrderRequest> entity =
                new HttpEntity<>(orderRequest, headers);

        restTemplate.put(
                url,
                entity
        );

        log.info("Commande mise à jour avec succès");
    }

    /**
     * DELETE - Annule une commande.
     */
    public void cancelOrder(String orderId) {

        log.info(
                "Annulation de la commande : {}",
                orderId
        );

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/orders/{orderId}")
                .buildAndExpand(orderId)
                .toUriString();

        HttpHeaders headers = createHeaders();

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                entity,
                Void.class
        );

        log.info("Commande annulée avec succès");
    }

    /**
     * GET avec paramètres de requête.
     */
    public List<SupplierProductDTO> searchProducts(
            String category,
            Double maxPrice
    ) {

        log.info(
                "Recherche de produits - Catégorie : {}, Prix max : {}",
                category,
                maxPrice
        );

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/products/search")
                .queryParam("category", category)
                .queryParam("maxPrice", maxPrice)
                .toUriString();

        HttpHeaders headers = createHeaders();

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<SupplierProductDTO>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<List<SupplierProductDTO>>() {
                        }
                );

        return response.getBody();
    }

    /**
     * Crée les headers avec authentification.
     */
    private HttpHeaders createHeaders() {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", apiKey);
        headers.set("User-Agent", "Jardinage-App/1.0");

        return headers;
    }
}