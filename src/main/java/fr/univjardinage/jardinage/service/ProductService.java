package fr.univjardinage.jardinage.service;

import fr.univjardinage.jardinage.dto.CreateProductDTO;
import fr.univjardinage.jardinage.dto.ProductDTO;
import fr.univjardinage.jardinage.entity.ProductCategory;

import fr.univjardinage.jardinage.exception.DuplicateProductException;
import fr.univjardinage.jardinage.exception.InvalidPriceException;
import fr.univjardinage.jardinage.exception.ProductNotFoundException;
import fr.univjardinage.jardinage.exception.InsufficientStockException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service de gestion des produits de jardinage
 */
public interface ProductService{

    /**
     * Cree un nouveau produit
     *
     * @param createDto les donnees du produit
     * @return le produit cree
     * @throws DuplicateProductException si le nom existe deja
     * @throws InvalidPriceException si le prix est invalide
     */

    /**
     * RBAC : Seuls les VENDOR, MANAGER et ADMIN peuvent creer
     */
    @PreAuthorize("hasAnyRole('VENDOR', 'MANAGER', 'ADMIN')")
    ProductDTO createProduct(CreateProductDTO createDto);

    /**
     * Recherche un produit par son ID
     *
     * @param id l’identifiant du produit
     * @return le produit trouve
     * @throws ProductNotFoundException si le produit n’existe pas
     */
    /**
     * RBAC : Lecture accessible a tous les utilisateurs authentifies
     */
    @PreAuthorize("isAuthenticated()")
    ProductDTO getProductById(Long id);

    /**
     * Liste tous les produits actifs
     *
     * @return la liste des produits actifs
     */
    List<ProductDTO> getAllActiveProducts();


    /**
     * Recherche les produits par categorie
     *
     * @param category la categorie recherchee
     * @return la liste des produits de cette categorie
     */
    List<ProductDTO> getProductsByCategory(ProductCategory category);

    /**
     * Met a jour un produit existant
     *
     * @param id l’identifiant du produit
     * @param productDTO les nouvelles donnees
     * @return le produit mis a jour
     * @throws ProductNotFoundException si le produit n’existe pas
     */

    /**
     * ABAC : Modification autorisee si :
     * - ADMIN : toujours autorise
     * - MANAGER : produit actif
     * - VENDOR : proprietaire du produit
     */
    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "(hasRole('MANAGER') and " +
                    "@productSecurityService.isProductActive(#id)) or " +
                    "(hasRole('VENDOR') and " +
                    "@productSecurityService.isProductOwner(#id))"
    )
    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    /**
     * ABAC : Suppression autorisee uniquement pour ADMIN
     */

    /**
     * Supprime logiquement un produit(desactivation)
     *
     * @param id l’identifiant du produit
     * @throws ProductNotFoundException si le produit n’existe pas
     */

    @PreAuthorize("hasRole('ADMIN')")
    void deleteProduct(Long id);


    /**
     * Ajuste le stock d ’ un produit
     *
     * @param id l ’ identifiant du produit
     * @param quantity la quantite a ajouter(negative pour retirer)
     * @return le produit mis a jour
     * @throws ProductNotFoundException si le produit n ’ existe pas
     * @throws InsufficientStockException si le stock est insuffisant
     */
    /**
     * ABAC : Ajustement de stock avec verification de la quantite
     * VENDOR peut ajouter du stock mais pas en retirer
     */
    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "hasRole('MANAGER') or " +
                    "(hasRole('VENDOR') and #quantity > 0)"
    )
    ProductDTO adjustStock(Long id, Integer quantity);

    /**
     * Applique une remise sur un produit
     *
     * @param id l ’ identifiant du produit
     * @param discountPercentage pourcentage de remise
     * @return le produit avec le nouveau prix
     * @throws ProductNotFoundException si le produit n ’ existe pas
     * @throws InvalidPriceException si la remise est invalide
     */
    /**
     * ABAC : Remise limitee selon le role
     * - ADMIN : pas de limite
     * - MANAGER : maximum 30%
     * - VENDOR : maximum 10%
     */
    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "(hasRole('MANAGER') and #discountPercentage <= 30) or " +
                    "(hasRole('VENDOR') and #discountPercentage <= 10)"
    )
    ProductDTO applyDiscount(
            Long id,
            BigDecimal discountPercentage
    );
    /**
     * Recherche les produits en rupture de stock
     *
     * @return la liste des produits en rupture
     */
    List<ProductDTO> getOutOfStockProducts();

    /**
     * Recherche les produits avec stock faible
     *
     * @param threshold le seuil de stock
     * @return la liste des produits avec stock faible
     */
    List<ProductDTO> getLowStockProducts(Integer threshold);


    /**
     * ABAC avec Post-Authorization
     * Filtre les resultats selon le departement de l'utilisateur
     */
    @PostAuthorize(
            "hasRole('ADMIN') or " +
                    "returnObject.department == authentication.principal.department"
    )
    ProductDTO getProductWithDepartmentCheck(Long id);
}