package fr.univjardinage.jardinage.mapper;

import    fr.univjardinage.jardinage.dto.ProductDTO;
import    fr.univjardinage.jardinage.entity.Product;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Mapper(componentModel = "spring")
public interface ProductMapperAdvanced{

/**
 * Mapping avec calcul du prix TTC(TVA 20%)
 */
    @Mapping(target = "price",
            expression = "java(calculatePriceWithTax(entity.getPrice()))")
    ProductDTO toDtoWithTax(Product entity);

    /**
    * Mapping avec indicateur de disponibilite
    */
    @Mapping(target = "description", expression = "java(addStockInfo(entity))")
    ProductDTO toDtoWithStockInfo(Product entity);


    /**
     * Calcule le prix TTC
     */
    default BigDecimal calculatePriceWithTax(BigDecimal priceHT){
        if(priceHT == null){
            return BigDecimal.ZERO;
        }
        return priceHT.multiply(new BigDecimal("1.20"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
    * Ajoute l ’ information de stock a la description
     */
    default String addStockInfo(Product product){
        if (product == null) {
            return "[RUPTURE DE STOCK] ";
        }

        String baseDescription = product.getDescription() != null
                ? product.getDescription()
                : "";

        String stockInfo;
        if (product.getStock() == null || product.getStock() == 0) {
            stockInfo = "[RUPTURE DE STOCK] ";
        } else if(product.getStock()<10){
            stockInfo = "[Stock limite : " + product.getStock() + " unites] ";
        } else{
            stockInfo = "[En stock] ";
        }


        return baseDescription + stockInfo;
    }

    /**
     * After - mapping pour validation
     */
    @AfterMapping
    default void validateProduct(@MappingTarget ProductDTO dto){
        if(dto.getPrice() != null && dto.getPrice().compareTo( BigDecimal.ZERO)<0){
            throw new IllegalArgumentException(" Le prix ne peut pas etre negatif ");
        }
    }
}

