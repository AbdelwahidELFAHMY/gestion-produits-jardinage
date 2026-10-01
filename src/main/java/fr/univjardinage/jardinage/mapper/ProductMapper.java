package fr.univjardinage.jardinage.mapper;


import   fr.univjardinage.jardinage.dto.CreateProductDTO;
import   fr.univjardinage.jardinage.dto.ProductDTO;
import   fr.univjardinage.jardinage.entity.Product;
import   org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductMapper{

    /**
     * Convertit une entite Product vers ProductDTO
     */
    @Named("toDto")
    ProductDTO toDto(Product entity);

    /**
     * Convertit un ProductDTO vers une entite Product
     */
    Product toEntity(ProductDTO dto);

    /**
     * Convertit une liste d ’ entites vers une liste de DTOs
     */
    @IterableMapping(qualifiedByName = "toDto")
    List<ProductDTO> toDtoList(List<Product> entities);

/**
 * Convertit un CreateProductDTO vers une entite Product
 * Par defaut, le produit est actif
 */

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "creationDate", ignore = true)
  @Mapping(target = "active", constant = "true")
  Product toEntityFromCreate(CreateProductDTO createDto);

/**
 * Met a jour une entite existante avec les donnees d ’ un DTO
 * L ’ ID et la date de creation ne sont pas modifies
 */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    void updateEntityFromDto(ProductDTO dto, @MappingTarget Product entity);


/**
 * Mapping personnalise avec expression
 */
    @Mapping(target = "description", expression = "java(entity.getDescription() == null || entity.getDescription().isEmpty() ? \"Aucune description disponible\" : entity.getDescription().trim())")
    ProductDTO toDtoWithFormattedDescription(Product entity);
}

