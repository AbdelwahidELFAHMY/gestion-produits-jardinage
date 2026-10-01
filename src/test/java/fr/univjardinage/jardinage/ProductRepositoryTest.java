package fr.univjardinage.jardinage;

import fr.univjardinage.jardinage.entity.Product ;
import fr . univjardinage . jardinage .entity.ProductCategory ;
import fr.univjardinage.jardinage.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import java . math . BigDecimal ;
import java . util . List ;
import java . util . Optional ;

import static org . assertj . core . api . Assertions .*;

@DataJpaTest
@DisplayName ( " Tests du repository Product " )
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository ;

    @Autowired
    private TestEntityManager entityManager ;

    private Product rosier ;
    private Product tondeuse ;
    private Product engrais ;

    @BeforeEach
    void setUp () {
        // Preparation des donnees de test
        rosier = Product . builder ()
                . name ( " Rosier grimpant " )
                . category ( ProductCategory . PLANTE )
                . price ( new BigDecimal ( "15.99" ) )
                . stock (50)
                . description ( " Rosier grimpant rouge " )
                . active ( true )
                . build () ;

        tondeuse = Product . builder ()
                . name ( " Tondeuse electrique " )
                . category ( ProductCategory . OUTIL )
                . price ( new BigDecimal ( "299.99" ) )
                . stock (5)
                . description ( " Tondeuse 1500 W " )
                . active ( true )
                . build () ;

        engrais = Product . builder ()
                . name ( " Engrais bio " )
                . category ( ProductCategory . ENGRAIS )
                . price ( new BigDecimal ( "12.50" ) )
                . stock (0)
                . description ( " Engrais biologique 5 kg " )
                . active ( false )
                . build () ;

        entityManager . persist ( rosier ) ;
        entityManager . persist ( tondeuse ) ;
        entityManager . persist ( engrais ) ;
        entityManager . flush () ;

    }

    @Test
    @DisplayName ( " Doit sauvegarder un produit avec succes " )
    void testSaveProduct () {
// Given
        Product newProduct = Product . builder ()
                . name ( " Secateur " )
                . category ( ProductCategory . OUTIL )
                . price ( new BigDecimal ( "25.00" ) )
                . stock (100)
                . active ( true )
                . build () ;

// When
        Product saved = productRepository . save ( newProduct ) ;

        // Then
        assertThat ( saved ) . isNotNull () ;
        assertThat ( saved . getId () ) . isNotNull () ;
        assertThat ( saved . getCreationDate () ) . isNotNull () ;

    }

    @Test
    @DisplayName ( " Doit trouver les produits par categorie " )
    void testFindByCategory () {
// When
        List <Product> plantes = productRepository.findByCategory (ProductCategory.PLANTE ) ;

// Then
        assertThat ( plantes )
                . hasSize (1)
                . extracting ( Product :: getName )
                . contains ( " Rosier grimpant " ) ;

    }

    @Test
    @DisplayName ( " Doit trouver uniquement les produits actifs " )
    void findByActiveTrue() {
        // When
        List < Product > activeProducts = productRepository.findByActiveTrue () ;

        // Then
        assertThat ( activeProducts )
                . hasSize (2)
                . extracting ( Product :: getActive )
                . containsOnly ( true ) ;

    }
    @Test
    @DisplayName ( " Doit trouver les produits par nom ( insensible a la casse ) " )
    void testfindByNameContainingIgnoreCase () {
// When
        List < Product > results = productRepository.findByNameContainingIgnoreCase ( " ROSIER " ) ;

// Then
        assertThat( results )
                . hasSize (1)
                . first ()
                . extracting ( Product :: getName )
                . isEqualTo ( " Rosier grimpant " ) ;
    }
    @Test
    @DisplayName ( " Doit trouver les produits dans une fourchette de prix "
    )
    void testFindByPriceBetween () {
// When
        List < Product > products = productRepository.findByPriceBetween (
                new BigDecimal ( "10.00" ) ,
                new BigDecimal ( "20.00" ));
// Then
        assertThat ( products )
                . hasSize (2)
                . extracting ( Product :: getName )
                . containsExactlyInAnyOrder ( " Rosier grimpant " , " Engrais bio " ) ;
    }

    @Test
    @DisplayName ( "Doit compter les produits par categorie" )
    void testCountByCategory () {
        // When
        Long count = productRepository.countByCategory ( ProductCategory.OUTIL ) ;

        // Then
        assertThat (count).isEqualTo(1) ;
    }
    @Test
    @DisplayName ( " Doit verifier l’existence d’un produit par nom " )
    void testExistsByNameIgnoreCase() {
        // When
        Boolean exists = productRepository.existsByNameIgnoreCase ( " tondeuse electrique " ) ;

        // Then
        assertThat ( exists ) . isTrue () ;
    }

    @Test
    @DisplayName ( " Doit trouver les produits en rupture de stock " )
    void testFindLowStockProducts() {
// When
        List < Product > lowStock = productRepository.findLowStockProducts(10) ;
        // Then
        assertThat ( lowStock )
                . hasSize (1)
                . first ()
                . extracting ( Product :: getName )
                . isEqualTo ( " Tondeuse electrique " ) ;
    }
    @Test
    @DisplayName ( " Doit trouver les categories actives " )
    void testFindAllActiveCategories  () {
        // When
        List < ProductCategory > categories = productRepository.findAllActiveCategories() ;
        // Then
        assertThat ( categories )
                . hasSize (2)
                . contains ( ProductCategory . PLANTE , ProductCategory . OUTIL );
    }
    @Test
    @DisplayName ( " Doit supprimer un produit par ID " )
    void testDeleteProduct () {
        // Given
        Long productId = rosier.getId();

        // When
        productRepository.deleteById ( productId ) ;
        Optional < Product > deleted = productRepository.findById(productId);
        // Then
        assertThat(deleted).isEmpty () ;
    }
}