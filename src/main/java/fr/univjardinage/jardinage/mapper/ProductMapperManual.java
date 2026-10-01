package fr.univjardinage.jardinage.mapper;

import   fr.univjardinage.jardinage.dto.CreateProductDTO;
import   fr.univjardinage.jardinage.dto.ProductDTO;
import   fr.univjardinage.jardinage.entity.Product;
import   org.springframework.stereotype.Component;


@Component
public class ProductMapperManual implements EntityMapper<Product, ProductDTO>{

    @Override
    public ProductDTO toDto(Product entity){
        if(entity == null){
            return null;
        }

        return ProductDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .description(entity.getDescription())
                .active(entity.getActive())
                .creationDate(entity.getCreationDate())
                .build();
    }

    @Override
    public Product toEntity(ProductDTO dto){
        if(dto == null){
            return null;
        }

        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .category(dto.getCategory())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .description(dto.getDescription())
                .active(dto.getActive())
                .creationDate(dto.getCreationDate())
                .build();
    }

/**
 * Convertit un CreateProductDTO vers une entite Product
 *
 * @param createDto le DTO de creation
 * @return l ’ entite Product
 */
    public Product toEntityFromCreate(CreateProductDTO createDto){
        if(createDto == null){
            return null;
        }

        return Product.builder()
                .name(createDto.getName())
                .category(createDto.getCategory())
                .price(createDto.getPrice())
                .stock(createDto.getStock())
                .description(createDto.getDescription())
                .active(true) // Par defaut actif
                .build();


    }


/**
 * Met a jour une entite existante avec les donnees d ’ un DTO
 *
 * @param dto le DTO source
 * @param entity l ’ entite a mettre a jour
 */
    public void updateEntityFromDto(ProductDTO dto, Product entity){
        if(dto == null || entity == null){
            return;
        }

        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.getActive());
    }
}


