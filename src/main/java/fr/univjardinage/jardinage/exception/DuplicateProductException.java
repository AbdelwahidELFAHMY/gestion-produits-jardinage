package fr.univjardinage.jardinage.exception;

public class DuplicateProductException extends ProductException{
    public DuplicateProductException(String productName){
        super(" Un produit avec le nom ’" + productName + " ’ existe deja ", " DUPLICATE_PRODUCT ");
    }
}

