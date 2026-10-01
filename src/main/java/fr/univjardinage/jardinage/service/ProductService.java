package fr.univjardinage.jardinage.service;

import fr.univjardinage.jardinage.dto.CreateProductDTO;
import fr.univjardinage.jardinage.dto.ProductDTO;
import fr.univjardinage.jardinage.entity.ProductCategory;

import fr.univjardinage.jardinage.exception.DuplicateProductException;
import fr.univjardinage.jardinage.exception.InvalidPriceException;
import fr.univjardinage.jardinage.exception.ProductNotFoundException;
import fr.univjardinage.jardinage.exception.InsufficientStockException;

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
    ProductDTO createProduct(CreateProductDTO createDto);

    /**
     * Recherche un produit par son ID
     *
     * @param id l’identifiant du produit
     * @return le produit trouve
     * @throws ProductNotFoundException si le produit n’existe pas
     */
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
    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    /**
     * Supprime logiquement un produit(desactivation)
     *
     * @param id l’identifiant du produit
     * @throws ProductNotFoundException si le produit n’existe pas
     */
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
    ProductDTO applyDiscount(Long id, BigDecimal discountPercentage);

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
}