package fr.univjardinage.jardinage.dto ;

import fr.univjardinage.jardinage.entity.ProductCategory ;
import jakarta.validation.constraints.*;
import  fr.univjardinage.jardinage.validation.ValidPrice;
import lombok.*;

import java.math.BigDecimal ;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDTO {
    @NotBlank(message = "{product.name.notblank}")
    @Size(min = 3, max = 100, message = "{product.name.size}")
    private String name ;

    @NotNull(message = "{product.category.notnull}")
    private ProductCategory category ;

    @NotNull(message = "{product.price.notnull}")
    @DecimalMin(value = "0.01", message = "{product.price.min}")
    @DecimalMax(value = "999999.99", message = "{product.price.max}")
    @Digits(integer = 6, fraction = 2, message = "{product.price.format}")
    @ValidPrice
    private BigDecimal price ;

    @NotNull(message = "{product.stock.notnull}")
    @Min(value = 0, message = "{product.stock.min}")
    @Max(value = 100000, message = "{product.stock.max}")
    private Integer stock ;

    @Size(max = 500, message = "{product.description.size}")
    private String description ;
}