package com.timepath.io.struct;

import java.lang.annotation.*;

/**
 *
 * @author TimePath
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface StructField {
    int index();
    boolean reverse() default false;
    int skip() default 0;
    int limit() default 0;
}
