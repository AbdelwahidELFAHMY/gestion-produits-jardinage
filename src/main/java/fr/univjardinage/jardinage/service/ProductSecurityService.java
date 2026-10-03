package fr.univjardinage.jardinage.service;

import fr.univjardinage.jardinage.entity.Product;
import fr.univjardinage.jardinage.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;

@Service("productSecurityService")
@RequiredArgsConstructor
public class ProductSecurityService {

    private final ProductRepository productRepository;

    /**
     * Verifie si le produit est actif
     */
    public boolean isProductActive(Long productId) {
        return productRepository.findById(productId)
                .map(Product::getActive)
                .orElse(false);
    }

    /**
     * Verifie si l'utilisateur connecte est proprietaire du produit
     * Pour simplification, on verifie via l'attribut createdBy.
     */
    public boolean isProductOwner(Long productId) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String currentUsername = auth.getName();

        return productRepository.findById(productId)
                .map(product ->
                        currentUsername.equals(product.getCreatedBy())
                )
                .orElse(false);
    }

    /**
     * Verifie si le produit appartient au meme departement
     * que l'utilisateur.
     */
    public boolean isSameDepartment(Long productId) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        // Recuperer le departement de l'utilisateur depuis le principal
        // Implementation simplifiee
        String userDepartment = getUserDepartment(auth);

        return productRepository.findById(productId)
                .map(product ->
                        userDepartment.equals(product.getDepartment())
                )
                .orElse(false);
    }

    /**
     * Verifie si une operation est autorisee selon l'heure.
     * Exemple : operations critiques seulement entre 8h et 18h.
     */
    public boolean isWithinBusinessHours() {

        int hour = LocalTime.now().getHour();

        return hour >= 8 && hour < 18;
    }

    /**
     * Verifie si la remise est dans les limites du role.
     */
    public boolean isDiscountAllowed(BigDecimal discount) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        // ADMIN : pas de limite
        if (auth.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN"))) {

            return true;
        }

        // MANAGER : maximum 30%
        if (auth.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_MANAGER"))) {

            return discount.compareTo(
                    new BigDecimal("30")
            ) <= 0;
        }

        // VENDOR : maximum 10%
        if (auth.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_VENDOR"))) {

            return discount.compareTo(
                    new BigDecimal("10")
            ) <= 0;
        }

        return false;
    }

    /**
     * Recupere le departement de l'utilisateur.
     * Implementation a adapter selon votre modele UserDetails.
     */
    private String getUserDepartment(Authentication auth) {

        return "DEFAULT";
    }
}