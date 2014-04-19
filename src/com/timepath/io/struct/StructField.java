package com.timepath.io.struct;

import java.lang.annotation.*;

/**
 * Struct field marker.
 * <p/>
 * Nested Object fields must either be statically accessible
 * via the nullary constructor, or pre-instantiated.
 * <p/>
 * Arrays must also be instantiated.
 * <p/>
 * @author TimePath
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface StructField {

    int index() default 0;

    boolean reverse() default false;

    int skip() default 0;

    int limit() default 0;

}
