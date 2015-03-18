package com.timepath.io.struct

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Struct field marker.
 * Nested Object fields must either be statically accessible
 * via the nullary constructor, or pre-instantiated.
 * Arrays must also be instantiated.
 *
 * @author TimePath
 */
Retention(RetentionPolicy.RUNTIME)
Target(ElementType.FIELD)
annotation public class StructField(public val index: Int = 0,
                                    /**
                                     * @return read with reverse byte order?
                                     */
                                    public val reverse: Boolean = false,
                                    /**
                                     * @return bytes to skip after this field
                                     */
                                    public val skip: Int = 0,
                                    /**
                                     * @return maximum length (mostly used for zstrings)
                                     */
                                    public val limit: Int = 0, public val nullable: Boolean = false)
