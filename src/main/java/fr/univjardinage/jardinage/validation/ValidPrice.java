package fr.univjardinage.jardinage.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;


import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PriceValidator.class)
@Documented
public @interface ValidPrice{

    String message() default "{product.price.invalid}";

    Class<?>[] groups() default{};


    Class<? extends Payload>[] payload() default{};
}

