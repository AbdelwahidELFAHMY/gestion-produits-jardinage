package fr.univjardinage.jardinage.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierProductDTO {

    private String productCode;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer availableStock;
    private String supplierName;
}