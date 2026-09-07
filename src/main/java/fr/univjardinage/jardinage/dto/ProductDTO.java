package fr.univjardinage.jardinage.dto ;

import fr.univjardinage.jardinage.entity.ProductCategory ;
import lombok.*;
import java.math.BigDecimal ;
import java.time.LocalDateTime ;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id ;
    private String name ;
    private ProductCategory category ;
    private BigDecimal price ;
    private Integer stock ;
    private String description ;
    private Boolean active ;
    private LocalDateTime creationDate ;
}