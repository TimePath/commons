package com.timepath.io.struct

/**
 * Struct field marker.
 * Nested Object fields must either be statically accessible
 * via the nullary constructor, or pre-instantiated.
 * Arrays must also be instantiated.
 *
 * @author TimePath
 */
@Retention
@Target(AnnotationTarget.FIELD)
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
