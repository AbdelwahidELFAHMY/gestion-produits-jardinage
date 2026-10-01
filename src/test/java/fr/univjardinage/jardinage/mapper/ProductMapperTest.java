package fr.univjardinage.jardinage.mapper;

import   fr.univjardinage.jardinage.dto.CreateProductDTO;
import   fr.univjardinage.jardinage.dto.ProductDTO;
import   fr.univjardinage.jardinage.entity.Product;
import   fr.univjardinage.jardinage.entity.ProductCategory;
import   org.junit.jupiter.api.DisplayName;
import   org.junit.jupiter.api.Test;
import   org.springframework.beans.factory.annotation.Autowired;
import   org.springframework.boot.test.context.SpringBootTest;

import   java.math.BigDecimal;
import   java.time.LocalDateTime;
import   java.util.Arrays;
import   java.util.List;


import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DisplayName(" Tests du ProductMapper MapStruct ")
class ProductMapperTest{


    @Autowired
    private ProductMapper productMapper;

    @Test
    @DisplayName(" Doit convertir une entite vers un DTO ")
    void testToDto(){
// Given
        Product product = Product.builder()
                .id(1L)
                .name(" Rosier ")
                .category(ProductCategory.PLANTE)
                .price(new BigDecimal("15.99"))
                .stock(50)
                .description(" Beau rosier rouge ")
                .active(true)
                .creationDate(LocalDateTime.now())
                .build();


// When
        ProductDTO dto = productMapper . toDto ( product ) ;

// Then
        assertThat ( dto ) . isNotNull () ;
        assertThat ( dto . getId () ) . isEqualTo (1L ) ;
        assertThat ( dto . getName () ) . isEqualTo ( " Rosier " ) ;
        assertThat ( dto . getCategory () ) . isEqualTo ( ProductCategory . PLANTE ) ;
        assertThat ( dto . getPrice () ) . isEqualByComparingTo ( new BigDecimal ( "15.99" ) ) ;
        assertThat ( dto . getStock () ) . isEqualTo (50) ;
        assertThat ( dto . getActive () ) . isTrue () ;
    }

    @Test
    @DisplayName ( " Doit convertir un DTO vers une entite " )
    void testToEntity () {
// Given
        ProductDTO dto = ProductDTO.builder()
                .id(1L)
                .name(" Tondeuse ")
                .category(ProductCategory.OUTIL)
                .price(new BigDecimal("299.99"))
                .stock(10)
                .active(true)
                .build();

// When
        Product entity = productMapper.toEntity(dto);

// Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(" Tondeuse ");
        assertThat(entity.getCategory()).isEqualTo(ProductCategory.OUTIL);
    }


    @Test
    @DisplayName(" Doit convertir un CreateProductDTO vers une entite ")
    void testToEntityFromCreate(){
// Given
        CreateProductDTO createDto = CreateProductDTO.builder()
                .name(" Engrais bio ")
                .category(ProductCategory.ENGRAIS)
                .price(new BigDecimal("12.50"))
                .stock(100)
                .description(" Engrais biologique ")
                .build();


// When
        Product entity = productMapper.toEntityFromCreate(createDto);

// Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo(" Engrais bio ");
        assertThat(entity.getActive()).isTrue();// Valeur par defaut
    }

    @Test
    @DisplayName(" Doit mettre a jour une entite existante ")
    void testUpdateEntityFromDto(){
// Given
        Product existingProduct = Product.builder()
                .id(1L)
                .name(" Ancien nom ")
                .category(ProductCategory.PLANTE)
                .price(new BigDecimal("10.00"))
                .stock(5)
                .active(false)
                .creationDate(LocalDateTime.now().minusDays(10))
                .build();

        ProductDTO updateDto = ProductDTO.builder()
                .name(" Nouveau nom ")
                .category(ProductCategory.OUTIL)
                .price(new BigDecimal("25.00"))
                .stock(50)
                .active(true)
                .build();

        LocalDateTime originalCreationDate = existingProduct.getCreationDate();

// When
        productMapper.updateEntityFromDto(updateDto, existingProduct);

// Then
        assertThat(existingProduct.getId()).isEqualTo(1L);// ID inchange
        assertThat(existingProduct.getName()).isEqualTo(" Nouveau nom ");
        assertThat(existingProduct.getPrice()).isEqualByComparingTo(new BigDecimal("25.00"));
        assertThat(existingProduct.getCreationDate()).isEqualTo(originalCreationDate);// Date inchangee
    }

    @Test
    @DisplayName(" Doit convertir une liste d ’ entites ")
    void testToDtoList(){
// Given
        List<Product> products = Arrays.asList(
                Product.builder().name(" Produit 1 ").category(
                                ProductCategory.PLANTE).price(new BigDecimal("10")).
                        stock(5).active(true).build(),
                Product.builder().name(" Produit 2 ").category(
                                ProductCategory.OUTIL).price(new BigDecimal("20")).
                        stock(10).active(true).build()
        );

// When
        List<ProductDTO> dtos = productMapper.toDtoList(products);

// Then
        assertThat(dtos)
                .hasSize(2)
                .extracting(ProductDTO :: getName)
                .containsExactly(" Produit 1 ", " Produit 2 ");
    }

    @Test
    @DisplayName(" Doit gerer les valeurs null ")
    void testNullHandling(){
// When
        ProductDTO dto = productMapper.toDto(null);
        Product entity = productMapper.toEntity(null);

// Then
        assertThat(dto).isNull();
        assertThat(entity).isNull();
    }
}

