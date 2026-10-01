package fr.univjardinage.jardinage.mapper;

import     fr.univjardinage.jardinage.entity.Product;
import     fr.univjardinage.jardinage.entity.ProductCategory;
import     org.junit.jupiter.api.Test;
import     org.springframework.beans.factory.annotation.Autowired;
import     org.springframework.boot.test.context.SpringBootTest;

import     java.math.BigDecimal;
import     java.time.LocalDateTime;
import     java.util.ArrayList;
import     java.util.List;


@SpringBootTest
class MapperPerformanceTest{

    @Autowired
    private ProductMapper productMapperMapStruct;


    @Autowired
    private ProductMapperManual productMapperManual;

    private static final int ITERATIONS = 100_000;

    @Test
    void compareMapperPerformance(){
// Preparation des donnees
        List<Product> products = new ArrayList<>();
        for(int i = 0;i<1000;i ++){
            products.add(Product.builder()
                    .id((long) i)
                    .name(" Produit " + i)
                    .category(ProductCategory.PLANTE)
                    .price(new BigDecimal("10.99"))
                    .stock(50)
                    .active(true)
                    .creationDate(LocalDateTime.now())
                    .build());
        }

// Test MapStruct
        long startMapStruct = System.currentTimeMillis();
        for(int i = 0;i<ITERATIONS / 1000;i ++){
            productMapperMapStruct.toDtoList(products);
        }
        long endMapStruct = System.currentTimeMillis();

// Test Manuel
        long startManual = System.currentTimeMillis();
        for(int i = 0;i<ITERATIONS / 1000;i ++){
            productMapperManual.toDtoList(products);
        }
        long endManual = System.currentTimeMillis();

// Resultats
        System.out.println(" MapStruct : " +(endMapStruct -       startMapStruct) + " ms ");
        System.out.println(" Manuel : " +(endManual - startManual) + " ms ");
    }
}