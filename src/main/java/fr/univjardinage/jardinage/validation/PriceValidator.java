
package fr.univjardinage.jardinage.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;


public class PriceValidator implements ConstraintValidator<ValidPrice, BigDecimal>{

    private static final BigDecimal MIN_PRICE = new BigDecimal("0.01");
    private static final BigDecimal MAX_PRICE = new BigDecimal("999999.99" ) ;

    @Override
    public void initialize(ValidPrice constraintAnnotation){
// Initialisation si necessaire
    }


    @Override
    public boolean isValid(BigDecimal price, ConstraintValidatorContext context){
        if(price == null){
            return true;// @NotNull gere ce cas
        }

// Verification : prix entre 0.01 et 999999.99
        if(price.compareTo(MIN_PRICE)<0 || price.compareTo(MAX_PRICE) > 0){
            return false;
        }

// Verification : maximum 2 decimales
        if(price.scale()> 2){
            return false;
        }

// Verification : pas de prix psychologiques(.99) pour les montants> 100
        if(price.compareTo(new BigDecimal("100"))> 0){
            BigDecimal cents = price.remainder(BigDecimal.ONE)
                    .multiply(new BigDecimal("100"));
            if(cents.compareTo(new BigDecimal("99")) == 0){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        " Les prix psychologiques(.99) ne sont pas autorises pour les montants superieurs a 100 EUR "
                ).addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}