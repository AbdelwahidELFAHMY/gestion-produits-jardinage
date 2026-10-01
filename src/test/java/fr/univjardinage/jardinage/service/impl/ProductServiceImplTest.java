package fr.univjardinage.jardinage.service.impl;

import   fr.univjardinage.jardinage.dto.CreateProductDTO;
import   fr.univjardinage.jardinage.dto.ProductDTO;
import   fr.univjardinage.jardinage.entity.Product;
import   fr.univjardinage.jardinage.entity.ProductCategory;
import   fr.univjardinage.jardinage.exception.*;
import   fr.univjardinage.jardinage.mapper.ProductMapper;
import   fr.univjardinage.jardinage.repository.ProductRepository;
import   org.junit.jupiter.api.BeforeEach;
import   org.junit.jupiter.api.DisplayName;
import   org.junit.jupiter.api.Test;
import   org.junit.jupiter.api.extension.ExtendWith;
import   org.mockito.InjectMocks;
import   org.mockito.Mock;
import   org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName(" Tests unitaires du ProductService ")
class ProductServiceImplTest{

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;


    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDTO productDTO;
    private CreateProductDTO createProductDTO;

    @BeforeEach
    void setUp(){
        product = Product.builder()
                .id(1L)
                .name(" Rosier ")
                .category(ProductCategory.PLANTE)
                .price(new BigDecimal("15.99"))
                .stock(50)
                .active(true)
                .build();

        productDTO = ProductDTO.builder()
                .id(1L)
                .name(" Rosier ")
                .category(ProductCategory.PLANTE)
                .price(new BigDecimal("15.99"))
                .stock(50)
                .active(true)
                .build();
        createProductDTO = CreateProductDTO.builder()
                .name(" Rosier ")
                .category(ProductCategory.PLANTE)
                .price(new BigDecimal("15.99"))
                .stock(50)
                .build();
    }

    @Test
    @DisplayName(" Doit creer un produit avec succes ")
    void testCreateProduct_Success(){
// Given
        when(productRepository.existsByNameIgnoreCase(anyString())).
                thenReturn(false);
        when(productMapper.toEntityFromCreate(any())).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(any())).thenReturn(productDTO);

// When
        ProductDTO result = productService.createProduct(
                createProductDTO);

// Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(" Rosier ");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName(" Doit lever une exception si le produit existe deja ")
    void testCreateProduct_DuplicateName(){
// Given
        when(productRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);

// When / Then
        assertThatThrownBy(() -> productService.createProduct(createProductDTO))
                .isInstanceOf(DuplicateProductException.class)
                .hasMessageContaining(" existe deja ");

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName(" Doit lever une exception si le prix est invalide ")
    void testCreateProduct_InvalidPrice(){
// Given
        createProductDTO.setPrice(new BigDecimal("-10"));

// When / Then
        assertThatThrownBy(() -> productService.createProduct(createProductDTO))
                .isInstanceOf(InvalidPriceException.class);
    }





    @Test
    @DisplayName(" Doit trouver un produit par ID ")
    void testGetProductById_Success(){
// Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDto(any())).thenReturn(productDTO);

// When
        ProductDTO result = productService.getProductById(1L);

// Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName(" Doit lever une exception si le produit n ’ existe pas ")
    void testGetProductById_NotFound(){
// Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

// When / Then
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName(" Doit ajuster le stock avec succes ")
    void testAdjustStock_Success(){
// Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toDto(any())).thenReturn(productDTO);

// When
        ProductDTO result = productService.adjustStock(1L, 10);

// Then
        assertThat(result).isNotNull();
        verify(productRepository).save(any());
    }

    @Test
    @DisplayName(" Doit lever une exception si le stock est insuffisant ")
    void testAdjustStock_InsufficientStock(){
// Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

// When / Then
        assertThatThrownBy(() -> productService.adjustStock(1L, -100))
                .isInstanceOf(InsufficientStockException.class);
    }


    @Test
    @DisplayName(" Doit appliquer une remise valide ")
    void testApplyDiscount_Success(){
// Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toDto(any())).thenReturn(productDTO);

// When
        ProductDTO result = productService.applyDiscount(1L, new BigDecimal("10"));
// Then
        assertThat(result).isNotNull();
        verify(productRepository).save(argThat(p ->
                p.getPrice().compareTo(new BigDecimal("14.39")) == 0
        ));
    }

    @Test
    @DisplayName("Doit lever une exception si la remise est invalide")
    void testApplyDiscount_InvalidDiscount() {

        // When / Then
        assertThatThrownBy(() ->
                productService.applyDiscount(1L, new BigDecimal("60"))
        )
                .isInstanceOf(InvalidPriceException.class);
    }

    @Test
    @DisplayName(" Doit supprimer logiquement un produit ")
    void testDeleteProduct_Success(){
// Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

// When
        productService.deleteProduct(1L);

// Then
        verify(productRepository).save(argThat(p -> ! p.getActive()));
    }
}
