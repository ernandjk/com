package com.google.errorprone.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

@Documented
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface InlineMe {
    String[] imports() default {};
    
    String replacement();
    
    String[] staticImports() default {};
}
