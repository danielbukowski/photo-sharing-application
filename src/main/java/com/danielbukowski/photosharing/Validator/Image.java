package com.danielbukowski.photosharing.Validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageValidator.class)
@Target( { ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Image {

    String message() default "Only images under 20 MB size with JPG or PNG extensions are supported.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
