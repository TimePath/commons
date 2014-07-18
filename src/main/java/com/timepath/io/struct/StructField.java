package com.timepath.io.struct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Struct field marker.
 * Nested Object fields must either be statically accessible
 * via the nullary constructor, or pre-instantiated.
 * Arrays must also be instantiated.
 *
 * @author TimePath
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StructField {

    int index() default 0;

    /**
     * @return read with reverse byte order?
     */
    boolean reverse() default false;

    /**
     * @return bytes to skip after this field
     */
    int skip() default 0;

    /**
     * @return maximum length (mostly used for zstrings)
     */
    int limit() default 0;

    boolean nullable() default false;
}
