package fr.univjardinage.jardinage.exception;

public class InvalidPriceException extends ProductException{
    public InvalidPriceException(String message){
        super(message, " INVALID_PRICE ");
    }
}