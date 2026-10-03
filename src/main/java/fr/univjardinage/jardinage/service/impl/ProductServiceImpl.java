package fr.univjardinage.jardinage.service.impl;

import fr.univjardinage.jardinage.dto.CreateProductDTO;


import   fr.univjardinage.jardinage.dto.ProductDTO;
import   fr.univjardinage.jardinage.entity.Product;
import   fr.univjardinage.jardinage.entity.ProductCategory;
import fr.univjardinage.jardinage.entity.User;
import   fr.univjardinage.jardinage.exception.*;
import   fr.univjardinage.jardinage.mapper.ProductMapper;
import   fr.univjardinage.jardinage.repository.ProductRepository;
import fr.univjardinage.jardinage.repository.UserRepository;
import   fr.univjardinage.jardinage.service.ProductService;
import   lombok.RequiredArgsConstructor;
import   lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import   org.springframework.stereotype.Service;
import   org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;

    private static final Integer LOW_STOCK_THRESHOLD = 10;
    private static final BigDecimal MAX_DISCOUNT = new BigDecimal("50");

    @Override
    @Transactional
    public ProductDTO createProduct(CreateProductDTO createDto){
        log.info(" Creation d’un nouveau produit :{} ", createDto.getName());

// Validation : verifier si le produit existe deja
        if(productRepository.existsByNameIgnoreCase(createDto.getName())){
            log.error(" Produit deja existant :{} ", createDto.getName());
            throw new DuplicateProductException(createDto.getName());
        }

// Validation : prix positif
        validatePrice(createDto.getPrice());

// Conversion et sauvegarde
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User owner = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                "Utilisateur authentifié introuvable : "
                        + authentication.getName()
        ));

        Product product = productMapper.toEntityFromCreate(createDto);
        product.setCreatedBy(owner);
        Product savedProduct = productRepository.save(product);

        log.info(" Produit cree avec succes - ID :{} ", savedProduct.getId());
        return productMapper.toDto(savedProduct);
    }

    @Override
    public ProductDTO getProductById(Long id){
        log.debug(" Recherche du produit avec ID :{} ", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));


        return productMapper.toDto(product);
    }

    @Override
    public List<ProductDTO> getAllActiveProducts(){
        log.debug(" Recuperation de tous les produits actifs ");

        List<Product> products = productRepository.findByActiveTrue();
        return productMapper.toDtoList(products);
    }

    @Override
    public List<ProductDTO> getProductsByCategory(ProductCategory
                                                          category){
        log.debug(" Recherche des produits de categorie :{} ", category);

        List<Product> products = productRepository.findByCategoryAndActiveTrue(category);
        return productMapper.toDtoList(products);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO){
        log.info(" Mise a jour du produit ID :{} ", id);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

// Validation du nouveau prix
        if(productDTO.getPrice() != null){
            validatePrice(productDTO.getPrice());
        }

// Mise a jour
        productMapper.updateEntityFromDto(productDTO, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);

        log.info(" Produit mis a jour avec succes - ID :{} ", id);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id){
        log.info(" Suppression logique du produit ID :{} ", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setActive(false);
        productRepository.save(product);

        log.info(" Produit desactive avec succes - ID :{} ", id);
    }

    @Override
    @Transactional
    public ProductDTO adjustStock(Long id, Integer quantity){
        log.info(" Ajustement du stock du produit ID :{} - Quantite :{} ", id, quantity);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Integer newStock = product.getStock() + quantity;

// Validation : stock ne peut pas etre negatif
        if(newStock<0){
            log.error(" Stock insuffisant pour le produit :{} ", product.
                    getName());
            throw new InsufficientStockException(
                    product.getName(),
                    Math.abs(quantity),
                    product.getStock()
            );
        }

        product.setStock(newStock);

// Alerte si stock faible
        if(newStock> 0 && newStock<= LOW_STOCK_THRESHOLD){
            log.warn(" ALERTE : Stock faible pour le produit{} - Stock actuel :{} ",
            product.getName(), newStock);
        }


// Desactiver si rupture de stock
        if(newStock == 0){
            log.warn(" Rupture de stock pour le produit :{} ", product.
                    getName());
            product.setActive(false);
        }

        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }


    @Override
    @Transactional
    public ProductDTO applyDiscount(Long id, BigDecimal discountPercentage){
        log.info(" Application d’une remise de{}% sur le produit ID :{} ",  discountPercentage, id);

// Validation : remise entre 0 et 50%
        if(discountPercentage.compareTo(BigDecimal.ZERO)<0 ||
                discountPercentage.compareTo(MAX_DISCOUNT)> 0){
            throw new InvalidPriceException(" La remise doit etre comprise entre 0 et 50% ");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        BigDecimal originalPrice = product.getPrice();
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                discountPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
        );
        BigDecimal newPrice = originalPrice.multiply(discountMultiplier)
                .setScale(2, RoundingMode.
                        HALF_UP);

        log.info(" Prix original :{} - Nouveau prix :{} ", originalPrice,
                newPrice);
        product.setPrice(newPrice);

        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    public ProductDTO getProductWithDepartmentCheck(Long id) {
        return null;
    }

    @Override
    public List<ProductDTO> getOutOfStockProducts(){
        log.debug(" Recherche des produits en rupture de stock ");

        List<Product> products = productRepository.findLowStockProducts(0);
        return productMapper.toDtoList(products);
    }

    @Override
    public List<ProductDTO> getLowStockProducts(Integer threshold){
        log.debug(" Recherche des produits avec stock<{} ", threshold);

        List<Product> products = productRepository.findLowStockProducts(
                threshold);
        return productMapper.toDtoList(products);
    }

    /**
     * Valide qu ’ un prix est positif et non nul
     */
    private void validatePrice(BigDecimal price){
        if(price == null || price.compareTo(BigDecimal.ZERO)<= 0){
            throw new InvalidPriceException(" Le prix doit etre strictement positif ");
        }
    }
}