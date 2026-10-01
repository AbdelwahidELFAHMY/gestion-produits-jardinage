package fr.univjardinage.jardinage.exception;

public class ProductNotFoundException extends ProductException{
    public ProductNotFoundException(Long id){
        super(" Produit non trouve avec l ’ ID : " + id, " PRODU CT_NOT_F OUND ");
    }
}