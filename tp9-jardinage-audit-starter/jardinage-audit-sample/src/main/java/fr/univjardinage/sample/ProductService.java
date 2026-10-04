package fr.univjardinage.sample;

import fr.univjardinage.audit.service.AuditService;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final AuditService auditService;

    public ProductService(AuditService auditService) {
        this.auditService = auditService;
    }

    public void createProduct(Long productId, String productName) {
        // Ici se trouverait l'appel au repository après la création du produit.
        auditService.audit(
                "CREATE",
                "Product",
                productId,
                "sample-user",
                null,
                productName
        );

        // Le batch par défaut vaut 100 : on force l'export pour la démonstration.
        auditService.flush();
    }
}
