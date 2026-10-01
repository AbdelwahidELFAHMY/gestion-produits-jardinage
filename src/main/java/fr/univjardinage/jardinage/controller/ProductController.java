package fr.univjardinage.jardinage.controller;

import    fr.univjardinage.jardinage.dto.CreateProductDTO;
import    fr.univjardinage.jardinage.dto.ProductDTO;
import    fr.univjardinage.jardinage.entity.ProductCategory;
import    fr.univjardinage.jardinage.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import    lombok.RequiredArgsConstructor;
import    lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;


@Slf4j
@Tag(name = "Products", description = "API de gestion des produits")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController{

    private final ProductService productService;

    /**
     * GET / api / v1 / products
     * Recupere tous les produits actifs
     */

    @Operation(
            summary = " Recuperer tous les produits ",
            description = " Retourne la liste de tous les produits actifs "
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = " 200 ", description = " Liste recuperee avec succes "),
            @ApiResponse(responseCode = " 500 ", description = " Erreur serveur ")
    })
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts(){
        log.info(" GET/api/v1/products - Recuperation de tous les produits ");
        List<ProductDTO> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * GET / api / v1 / products /{ id}
     * Recupere un produit par son ID
     */

    @Operation(summary = " Recuperer un produit par ID ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = " 200 ", description = " Produit trouve "),
            @ApiResponse(responseCode = " 404 ", description = " Produit non trouve ")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id){
        log.info("GET/api/v1/products/{} - Recuperation du produit ", id);
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * GET / api / v1 / products / category /{ category}
     * Recupere les produits par categorie
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(
            @PathVariable ProductCategory category){
        log.info("GET/api/v1/products/category/{} ", category);
        List<ProductDTO> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }


    /**
     * POST / api / v1 / products
     * Cree un nouveau produit
     */
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @RequestBody CreateProductDTO createDto){
        log.info("POST/api/v1/products - Creation d’un produit :{} ", createDto.getName());
        ProductDTO createdProduct = productService.createProduct(createDto);

        URI location = URI.create("/api/v1/products/" + createdProduct.getId());
        return ResponseEntity.created(location).body(createdProduct);
    }


    /**
     * PUT / api / v1 / products /{ id}
     * Met a jour un produit existant
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO){
        log.info("PUT/api/v1/products/{} - Mise a jour du produit ", id);
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * DELETE / api / v1 / products /{ id}
     * Supprime logiquement un produit
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        log.info(" DELETE/api/v1/products /{} - Suppression du produit ", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH / api / v1 / products /{ id}/ stock
     * Ajuste le stock d ’ un produit
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDTO> adjustStock(
            @PathVariable Long id,
            @RequestParam Integer quantity){
        log.info(" PATCH / api / v1 / products /{}/ stock - Ajustement :{} ", id, quantity);
        ProductDTO product = productService.adjustStock(id, quantity);
        return ResponseEntity.ok(product);
    }


    /**
     * PATCH / api / v1 / products /{ id}/ discount
     * Applique une remise sur un produit
     */
    @PatchMapping("/{id}/discount")
    public ResponseEntity<ProductDTO> applyDiscount(
            @PathVariable Long id,
            @RequestParam BigDecimal percentage){
        log.info(" PATCH / api / v1 / products /{}/ discount - Remise :{}% ", id, percentage);
        ProductDTO product = productService.applyDiscount(id, percentage);
        return ResponseEntity.ok(product);
    }

    /**
     * GET / api / v1 / products / search / low - stock
     * Recherche les produits avec stock faible
     */
    @GetMapping("/search/low-stock")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold){
        log.info(" GET / api / v1 / products / search / low - stock ? threshold = {} ", threshold);
        List<ProductDTO> products = productService.getLowStockProducts(
                threshold);
        return ResponseEntity.ok(products);
    }


    /**
     * GET / api / v1 / products / search / out - of - stock
     * Recherche les produits en rupture de stock
     */
    @GetMapping("/search/out-of-stock")
    public ResponseEntity<List<ProductDTO>> getOutOfStockProducts(){
        log.info(" GET / api / v1 / products / search / out - of - stock ");
        List<ProductDTO> products = productService.getOutOfStockProducts();
        return ResponseEntity.ok(products);
    }
}