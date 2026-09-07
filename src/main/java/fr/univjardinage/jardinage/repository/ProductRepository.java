package fr.univjardinage.jardinage.repository;

import fr.univjardinage.jardinage.entity.Product;
import fr.univjardinage.jardinage.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Query Methods - Spring Data genere automatiquement l’implementation
    List<Product> findByCategory (ProductCategory category ) ;

    List<Product> findByActiveTrue () ;

    List<Product> findByActiveFalse () ;

    List<Product> findByNameContainingIgnoreCase ( String name );

    List<Product> findByPriceBetween ( BigDecimal minPrice , BigDecimal maxPrice ) ;
    List<Product> findByCategoryAndActiveTrue ( ProductCategory category ) ;

    Optional<Product> findByNameIgnoreCase (String name ) ;

    Long countByCategory ( ProductCategory category ) ;

    Boolean existsByNameIgnoreCase ( String name ) ;

    // Requetes JPQL personnalisees
    @Query ( " SELECT p FROM Product p WHERE p.stock < : threshold AND p.active = true " )
    List < Product > findLowStockProducts ( @Param( " threshold " ) Integer threshold ) ;

    @Query ( " SELECT p FROM Product p WHERE p.category =: category ORDER BY p.price DESC " )
    List < Product > findByCategoryOrderByPriceDesc ( @Param ( " category " ) ProductCategory category ) ;
    @Query ( " SELECT DISTINCT p.category FROM Product p WHERE p.active = true " )
    List < ProductCategory > findAllActiveCategories () ;

    // Requete native SQL
    @Query ( value = " SELECT * FROM products WHERE price > :minPrice AND stock > 0 " , nativeQuery = true )
    List < Product > findAvailableProductsAbovePrice ( @Param ( " minPrice " ) BigDecimal minPrice ) ;
}