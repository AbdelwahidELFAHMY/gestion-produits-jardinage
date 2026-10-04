package fr.univjardinage.jardinage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAvailabilityDTO {

    private String productCode;
    private Boolean available;
    private Integer quantity;
    private String warehouseLocation;
    private Integer deliveryDays;
}