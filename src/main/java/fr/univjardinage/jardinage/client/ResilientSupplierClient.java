package fr.univjardinage.jardinage.client;

import java.util.List;

import fr.univjardinage.jardinage.dto.SupplierProductDTO;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResilientSupplierClient {

    private final SupplierClient supplierClient;

    public ResilientSupplierClient(SupplierClient supplierClient) {
        this.supplierClient = supplierClient;
    }

    /**
     * Récupère les produits avec Retry et Circuit Breaker.
     */
    @CircuitBreaker(
            name = "supplierService",
            fallbackMethod = "getProductsFallback"
    )
    @Retry(name = "supplierService")
    public List<SupplierProductDTO> getProductsWithResilience(
            String supplierId
    ) {

        return supplierClient.getProducts(supplierId);
    }

    /**
     * Méthode de fallback en cas d'échec.
     */
    private List<SupplierProductDTO> getProductsFallback(
            String supplierId,
            Exception ex
    ) {

        log.error(
                "Fallback activé pour le fournisseur : {}", supplierId, ex
        );

        return List.of();
    }
}