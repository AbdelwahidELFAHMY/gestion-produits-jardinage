package fr.univjardinage.jardinage.exception;

public class InsufficientStockException extends ProductException{
    public InsufficientStockException(String productName, Integer requested, Integer available){
        super(String.format(" Stock insuffisant pour %s.Demande : %d, Disponible : %d ", productName, requested, available), " INSUFFICIENT_STOCK ");
    }
}