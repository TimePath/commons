package com.timepath.io.struct

import com.timepath.io.OrderedInputStream
import com.timepath.io.OrderedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.reflect.Array
import java.lang.reflect.Field
import java.nio.charset.StandardCharsets
import java.util.Collections
import java.util.Comparator
import java.util.LinkedList
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.platform.platformStatic
import kotlin.properties.Delegates

public object Struct {

    private val LOG = Logger.getLogger(javaClass<Struct>().getName())

    /**
     * Calculates the size of non-dynamic structs. <b>Warning</b>: the class will be instantiated,
     * prefer using an existing instance. This constructor exists solely to catch misuse.
     *
     * @param clazz The struct class to measure
     * @return The size, or a value less than 0 to indicate dynamic size
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    deprecated("")
    throws(javaClass<IllegalArgumentException>(), javaClass<IllegalAccessException>(), javaClass<InstantiationException>())
    public platformStatic fun sizeof(clazz: Class<*>): Int {
        return sizeof(clazz.newInstance()!!)
    }

    /**
     * Calculates the size of non-dynamic structs
     *
     * @param instance An instance of the struct class to measure
     * @return The size, or a value less than 0 to indicate dynamic size
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    throws(javaClass<IllegalAccessException>(), javaClass<InstantiationException>())
    public platformStatic fun sizeof(instance: Any): Int {
        var size = 0
        for (field in instance.javaClass.getDeclaredFields()) {
            val accessible = field.isAccessible()
            field.setAccessible(true)
            val meta = field.getAnnotation(javaClass<StructField>())
            if (meta != null) {
                size += sizeof(field.getType(), meta, field[instance])
            }
            field.setAccessible(accessible)
        }
        return size
    }

    public platformStatic fun pack(instance: Any): ByteArray? {
        val baos = ByteArrayOutputStream()
        try {
            pack(instance, OrderedOutputStream(baos))
            return baos.toByteArray()
        } catch (ex: IOException) {
            LOG.log(Level.SEVERE, null, ex)
        } catch (ex: InstantiationException) {
            LOG.log(Level.SEVERE, null, ex)
        } catch (ex: IllegalAccessException) {
            LOG.log(Level.SEVERE, null, ex)
        } catch (ex: IllegalArgumentException) {
            LOG.log(Level.SEVERE, null, ex)
        }

        return null
    }

    throws(javaClass<IllegalAccessException>(), javaClass<IOException>(), javaClass<InstantiationException>())
    public platformStatic fun pack(instance: Any, os: OrderedOutputStream) {
        for (field in getFields(instance.javaClass)) {
            val accessible = field.isAccessible()
            field.setAccessible(true)
            writeField(instance, field, os)
            field.setAccessible(accessible)
        }
    }

    throws(javaClass<IOException>(), javaClass<IllegalAccessException>(), javaClass<InstantiationException>())
    private platformStatic fun writeField(instance: Any, field: Field, os: OrderedOutputStream) {
        val ref = field[instance]
        val meta = field.getAnnotation(javaClass<StructField>())
        os.write(ByteArray(meta.skip))
        val primitive = Primitive[field.getType()]
        if (primitive != null) {
            // Field is a primitive type
            primitive.write(ref, os, meta.limit)
        } else if (field.getType().isArray()) {
            // Field is an array
            if (ref == null) {
                // Check if instantiated
                if (meta.nullable) return
                throw InstantiationException("Cannnot instantiate array of unknown length")
            }
            writeArray(instance, field, os, 0)
        } else {
            // Field is a regular Object
            if (ref == null) {
                // Skip over
                LOG.log(Level.FINE, "Instantiating {0}", field)
                os.write(ByteArray(sizeof(instantiate(field.getType()))))
            } else {
                pack(ref, os)
            }
        }
    }

    throws(javaClass<IOException>(), javaClass<InstantiationException>(), javaClass<IllegalAccessException>())
    private platformStatic fun writeArray(instance: Any, field: Field, os: OrderedOutputStream, depth: Int) {
        val meta = field.getAnnotation(javaClass<StructField>())
        val dimensions = getArrayDepth(field.getType())
        val elemType = getArrayType(field.getType())
        val primitive = Primitive[elemType]
        val ref = field[instance]
        for (i in 0..Array.getLength(ref) - 1) {
            val elem = Array.get(ref, i)
            if (depth == dimensions) {
                // Not a nested array
                if (primitive != null) {
                    // Element is a primitive type
                    primitive.write(elem, os, meta.limit)
                } else {
                    if (elem == null) {
                        // Instantiate if needed
                        throw UnsupportedOperationException("Null objects not yet supported")
                    }
                    pack(elem, os)
                }
                Array.set(ref, i, elem)
            } else {
                writeArray(elem, field, os, depth + 1)
            }
        }
    }

    public platformStatic fun unpack(out: Any, vararg b: Byte) {
        try {
            unpack(out, OrderedInputStream(ByteArrayInputStream(b)))
        } catch (ex: IOException) {
            LOG.log(Level.SEVERE, null, ex)
        } catch (ex: InstantiationException) {
            LOG.log(Level.SEVERE, null, ex)
        } catch (ex: IllegalAccessException) {
            LOG.log(Level.SEVERE, null, ex)
        } catch (ex: IllegalArgumentException) {
            LOG.log(Level.SEVERE, null, ex)
        }

    }

    throws(javaClass<IOException>(), javaClass<IllegalAccessException>(), javaClass<InstantiationException>())
    public platformStatic fun unpack(instance: Any, `is`: OrderedInputStream) {
        for (field in getFields(instance.javaClass)) {
            val accessible = field.isAccessible()
            field.setAccessible(true)
            val `var` = readField(instance, field, `is`)
            field.set(instance, `var`)
            field.setAccessible(accessible)
        }
    }

    throws(javaClass<InstantiationException>())
    private fun instantiate(type: Class<*>): Any {
        val exStack = LinkedList<Throwable>()
        try {
            return type.newInstance()!!
        } catch (t: Throwable) {
            exStack.add(0, t)
        }

        try {
            val ctor = type.getDeclaredConstructors()[0]
            val accessible = ctor.isAccessible()
            ctor.setAccessible(true)
            val instance = ctor.newInstance()!!
            ctor.setAccessible(accessible)
            return instance
        } catch (t: Throwable) {
            exStack.add(0, t)
        }

        throw InstantiationException(exStack.toString())
    }

    private fun getArrayDepth(clazz: Class<*>): Int {
        return clazz.getName().lastIndexOf('[')
    }

    private fun getArrayType(clazz: Class<*>): Class<*> {
        var elemType = clazz
        for (i in 0..(getArrayDepth(clazz) + 1) - 1) {
            elemType = elemType.getComponentType()
        }
        return elemType
    }

    throws(javaClass<IOException>(), javaClass<InstantiationException>(), javaClass<IllegalAccessException>())
    private fun readArray(ref: Any, field: Field, `is`: OrderedInputStream, depth: Int) {
        val meta = field.getAnnotation(javaClass<StructField>())
        val dimensions = getArrayDepth(field.getType())
        val elemType = getArrayType(field.getType())
        val primitive = Primitive[elemType]
        for (i in 0..Array.getLength(ref) - 1) {
            var elem: Any? = Array.get(ref, i)
            if (depth == dimensions) {
                // Not a nested array
                if (primitive != null) {
                    // Element is a primitive type
                    elem = primitive.read(`is`, meta.limit)
                } else {
                    if (elem == null) {
                        // Instantiate if needed
                        LOG.log(Level.FINE, "Instantiating {0}", field)
                        elem = instantiate(elemType)
                    }
                    unpack(elem!!, `is`)
                }
                Array.set(ref, i, elem)
            } else {
                readArray(elem!!, field, `is`, depth + 1)
            }
        }
    }

    throws(javaClass<IOException>(), javaClass<IllegalArgumentException>(), javaClass<IllegalAccessException>(), javaClass<InstantiationException>())
    private fun readField(instance: Any, field: Field, `is`: OrderedInputStream): Any? {
        val meta = field.getAnnotation(javaClass<StructField>())
        `is`.skipBytes(meta.skip)
        var ref: Any?
        val primitive = Primitive[field.getType()]
        if (primitive != null) {
            // Field is a primitive type
            return primitive.read(`is`, meta.limit)
        } else if (field.getType().isArray()) {
            // Field is an array
            ref = field[instance]
            if (ref == null) {
                // Check if instantiated
                throw InstantiationException("Cannnot instantiate array of unknown length")
            }
            readArray(ref!!, field, `is`, 0)
        } else {
            // Field is a regular Object
            ref = field[instance]
            if (ref == null) {
                // Instantiate if needed
                LOG.log(Level.FINE, "Instantiating {0}", field)
                ref = instantiate(field.getType())
            }
            unpack(ref!!, `is`)
            field.set(instance, ref)
        }
        return ref
    }

    throws(javaClass<IllegalArgumentException>(), javaClass<IllegalAccessException>(), javaClass<InstantiationException>())
    private fun sizeof(type: Class<*>, meta: StructField, ref: Any?): Int {
        var size = 0
        val primitive = Primitive[type]
        if (primitive != null) {
            // Field is primitive
            var sz = primitive.size
            if (sz < 0) {
                if (meta.limit <= 0) {
                    // Dynamic length String
                    return Integer.MIN_VALUE
                }
                sz = meta.limit // Limit string
            }
            size += (if ((meta.limit > 0)) Math.min(sz, meta.limit) else sz) + meta.skip
        } else if (type.isArray()) {
            // Field is an array
            if (ref == null) {
                // Check if instantiated
                throw InstantiationException("Cannnot instantiate array of unknown length")
            }
            for (i in Array.getLength(ref).indices) {
                size += sizeof(type.getComponentType(), meta, Array.get(ref, i))
            }
        } else {
            // Field is a regular Object
            val sz = sizeof(ref ?: run {
                // Instantiate if needed
                LOG.log(Level.FINE, "Instantiating {0}", type)
                type.newInstance()!!
            })
            size += (if ((meta.limit > 0)) Math.min(sz, meta.limit) else sz) + meta.skip
        }
        return size
    }

    private fun getFields(clazz: Class<*>): List<Field> {
        val fields = clazz.getDeclaredFields()
        // Filter
        val al = LinkedList<Field>()
        for (ref in fields) {
            val field = ref.getAnnotation(javaClass<StructField>())
            if (field != null) {
                al.add(ref)
            }
        }
        // Sort
        Collections.sort<Field>(al, object : Comparator<Field> {
            override fun compare(o1: Field, o2: Field): Int {
                return o1.getAnnotation(javaClass<StructField>()).index - o2.getAnnotation(javaClass<StructField>()).index
            }
        })
        return al
    }

    private enum class Primitive(val type: String, val size: Int) {

        BYTE : Primitive("byte", 1)
        BOOLEAN : Primitive("boolean", 1)
        SHORT : Primitive("short", 2)
        CHAR : Primitive("char", 2)
        INT : Primitive("int", 4)
        FLOAT : Primitive("float", 4)
        LONG : Primitive("long", 8)
        DOUBLE : Primitive("double", 8)
        STRING : Primitive(javaClass<String>().getName(), -1)

        public companion object {
            private val vals by Delegates.lazy { Primitive.values().toMap { it.type } }
            public fun get(type: Class<*>): Primitive? = vals[type.getName()]
        }

        /**
         * Read a primitive
         *
         * @param is
         * @param limit Maximum amount of bytes to read
         * @return The primitive
         * @throws IOException
         */
        fun read(`is`: OrderedInputStream, limit: Int): Any? {
            when (this) {
                BOOLEAN -> return `is`.readBoolean()
                BYTE -> return `is`.readByte()
                CHAR -> return `is`.readChar()
                SHORT -> return `is`.readShort()
                INT -> return `is`.readInt()
                LONG -> return `is`.readLong()
                FLOAT -> return `is`.readFloat()
                DOUBLE -> return `is`.readDouble()
                STRING -> {
                    if (limit > 0) {
                        // Fixed size
                        val b = ByteArray(limit)
                        `is`.readFully(b)
                        return String(b, StandardCharsets.UTF_8)
                    }
                    return `is`.readString(limit) // NUL terminated
                }
                else -> return null
            }
        }

        public fun write(o: Any, os: OrderedOutputStream, limit: Int) {
            when (this) {
                BOOLEAN -> os.writeBoolean(o as Boolean)
                BYTE -> os.writeByte((o as Byte).toInt())
                CHAR -> os.writeChar((o as Char).toInt())
                SHORT -> os.writeShort((o as Short).toInt())
                INT -> os.writeInt(o as Int)
                LONG -> os.writeLong(o as Long)
                FLOAT -> os.writeFloat(o as Float)
                DOUBLE -> os.writeDouble(o as Double)
                STRING -> {
                    val s = o as String
                    val b = s.toByteArray(StandardCharsets.UTF_8)
                    if (limit > 0) {
                        // Fixed size
                        val min = Math.min(limit, b.size())
                        os.write(b, 0, min)
                        if (min == b.size()) os.write(ByteArray(limit - b.size())) // Padding
                    } else {
                        os.write(b, 0, b.size())
                        os.write(0) // NUL
                    }
                }
            }
        }
    }
}
