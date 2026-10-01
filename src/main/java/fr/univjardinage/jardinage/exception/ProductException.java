package fr.univjardinage.jardinage.exception;

public class ProductException extends RuntimeException{


    private final String errorCode;

    public ProductException(String message, String errorCode){
        super(message);
        this.errorCode = errorCode;
    }

    public ProductException(String message, String errorCode, Throwable cause){
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode(){
        return errorCode;
    }
}


