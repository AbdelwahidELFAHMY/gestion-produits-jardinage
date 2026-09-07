package fr.univjardinage.jardinage.dto ;

import fr.univjardinage.jardinage.entity.ProductCategory ;
import lombok.*;

import java.math.BigDecimal ;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDTO {
    private String name ;
    private ProductCategory category ;
    private BigDecimal price ;
    private Integer stock ;
    private String description ;
}