package fr.univjardinage.jardinage.mapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface generique pour les mappers Entity<-> DTO
 *
 * @param<E> Type de l ’ entite

 * @param<D> Type du DTO
 */
public interface EntityMapper<E, D>{

    /**
     * Convertit une entite vers un DTO
     *
     * @param entity l ’ entite a convertir
     * @return le DTO correspondant
     */
    D toDto(E entity);

    /**
     * Convertit un DTO vers une entite
     *
     * @param dto le DTO a convertir
     * @return l ’ entite correspondante
     */
    E toEntity(D dto);


    /**
     * Convertit une liste d ’ entites vers une liste de DTOs
     *
     * @param entities la liste d ’ entites
     * @return la liste de DTOs
     */
    default List<D> toDtoList(List<E> entities){
        return entities.stream()
                .map(this :: toDto)
                .collect(Collectors.toList());
    }


    /**
     * Convertit une liste de DTOs vers une liste d ’ entites
     *
     * @param dtos la liste de DTOs
     * @return la liste d ’ entites
     */
    default List<E> toEntityList(List<D> dtos){
        return dtos.stream()
                .map(this :: toEntity)
                .collect(Collectors.toList());
    }
}



