package com.timepath.io.struct;

import com.timepath.io.OrderedInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Struct {

    private static final Logger LOG = Logger.getLogger(Struct.class.getName());
    private static final Map<String, Primitive> primitiveTypes;

    static {
        Primitive[] values = Primitive.values();
        primitiveTypes = new HashMap<>(values.length);
        for(Primitive p : values) {
            primitiveTypes.put(p.type, p);
        }
    }

    private Struct() {
    }

    /**
     * Calculates the size of non-dynamic structs. <b>Warning</b>: the class will be instantiated,
     * prefer using an existing instance. This constructor exists solely to catch misuse.
     *
     * @param clazz
     *         The struct class to measure
     *
     * @return The size, or a value less than 0 to indicate dynamic size
     *
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Deprecated
    public static int sizeof(Class<?> clazz) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        return sizeof(clazz.newInstance());
    }

    /**
     * Calculates the size of non-dynamic structs
     *
     * @param instance
     *         An instance of the struct class to measure
     *
     * @return The size, or a value less than 0 to indicate dynamic size
     *
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static int sizeof(Object instance) throws IllegalAccessException, InstantiationException {
        int size = 0;
        for(Field field : instance.getClass().getDeclaredFields()) {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            StructField meta = field.getAnnotation(StructField.class);
            if(meta != null) {
                size += sizeof(field.getType(), meta, field.get(instance));
            }
            field.setAccessible(accessible);
        }
        return size;
    }

    public static void unpack(Object out, byte... b) {
        try {
            unpack(out, new OrderedInputStream(new ByteArrayInputStream(b)));
        } catch(IOException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static void unpack(Object instance, OrderedInputStream is)
    throws IOException, IllegalAccessException, InstantiationException
    {
        Field[] fields = instance.getClass().getDeclaredFields();
        // Filter
        List<Field> al = new LinkedList<>();
        for(Field ref : fields) {
            StructField field = ref.getAnnotation(StructField.class);
            if(field != null) {
                al.add(ref);
            }
        }
        // Sort
        Collections.sort(al, new Comparator<Field>() {
            @Override
            public int compare(Field o1, Field o2) {
                return o1.getAnnotation(StructField.class).index() - o2.getAnnotation(StructField.class).index();
            }
        });
        // Iterate
        for(Field field : al) {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Object var = readField(instance, field, is);
            field.set(instance, var);
            field.setAccessible(accessible);
        }
    }

    private static Object instantiate(Class<?> type) throws InstantiationException {
        List<Throwable> exStack = new LinkedList<>();
        try {
            return type.newInstance();
        } catch(Throwable t) {
            exStack.add(0, t);
        }
        try {
            Constructor<?> ctor = type.getDeclaredConstructors()[0];
            boolean accessible = ctor.isAccessible();
            ctor.setAccessible(true);
            Object instance = ctor.newInstance(new Object[0]);
            ctor.setAccessible(accessible);
            return instance;
        } catch(Throwable t) {
            exStack.add(0, t);
        }
        throw new InstantiationException(exStack.toString());
    }

    private static void readArray(Object ref, Field field, OrderedInputStream is, int depth)
    throws IOException, InstantiationException, IllegalAccessException
    {
        StructField meta = field.getAnnotation(StructField.class);
        int dimensions = field.getType().getName().lastIndexOf('[');
        Class<?> elemType = field.getType();
        for(int i = 0; i < ( dimensions + 1 ); i++) {
            elemType = elemType.getComponentType();
        }
        Primitive primitive = primitiveTypes.get(elemType.getName());
        for(int i = 0; i < Array.getLength(ref); i++) {
            Object elem = Array.get(ref, i);
            if(depth == dimensions) { // Not a nested array
                if(primitive != null) { // Element is a primitive type
                    elem = primitive.read(is, meta.limit());
                } else {
                    if(elem == null) { // Instantiate if needed
                        LOG.log(Level.FINE, "Instantiating {0}", field);
                        elem = instantiate(elemType);
                    }
                    unpack(elem, is);
                }
                Array.set(ref, i, elem);
            } else {
                readArray(elem, field, is, depth + 1);
            }
        }
    }

    private static Object readField(Object instance, Field field, OrderedInputStream is)
    throws IOException, IllegalArgumentException, IllegalAccessException, InstantiationException
    {
        StructField meta = field.getAnnotation(StructField.class);
        is.skipBytes(meta.skip());
        Object ref;
        Primitive primitive = primitiveTypes.get(field.getType().getName());
        if(primitive != null) { // Field is a primitive type
            return primitive.read(is, meta.limit());
        } else if(field.getType().isArray()) { // Field is an array
            ref = field.get(instance);
            if(ref == null) { // Check if instantiated
                throw new InstantiationException("Cannnot instantiate array of unknown length");
            }
            readArray(ref, field, is, 0);
        } else { // Field is a regular Object
            ref = field.get(instance);
            if(ref == null) { // Instantiate if needed
                LOG.log(Level.FINE, "Instantiating {0}", field);
                ref = instantiate(field.getType());
            }
            unpack(ref, is);
            field.set(instance, ref);
        }
        return ref;
    }

    private static int sizeof(Class<?> type, StructField meta, Object ref)
    throws IllegalArgumentException, IllegalAccessException, InstantiationException
    {
        int size = 0;
        Primitive primitive = primitiveTypes.get(type.getName());
        if(primitive != null) { // Field is primitive
            int sz = primitive.size;
            if(sz < 0) {
                if(meta.limit() <= 0) { // Dynamic length String
                    return Integer.MIN_VALUE;
                }
                sz = meta.limit(); // Limit string
            }
            size += ( ( meta.limit() > 0 ) ? Math.min(sz, meta.limit()) : sz ) + meta.skip();
        } else if(type.isArray()) { // Field is an array
            if(ref == null) { // Check if instantiated
                throw new InstantiationException("Cannnot instantiate array of unknown length");
            }
            for(int i = 0; i < Array.getLength(ref); i++) {
                size += sizeof(type.getComponentType(), meta, Array.get(ref, i));
            }
        } else { // Field is a regular Object
            if(ref == null) { // Instantiate if needed
                LOG.log(Level.FINE, "Instantiating {0}", type);
                ref = type.newInstance();
            }
            int sz = sizeof(ref);
            size += ( ( meta.limit() > 0 ) ? Math.min(sz, meta.limit()) : sz ) + meta.skip();
        }
        return size;
    }

    private enum Primitive {
        BOOLEAN("boolean", 1), BYTE("byte", 1), CHAR("char", 2), SHORT("short", 2), INT("int", 4),
        LONG("long", 4), FLOAT("float", 4), DOUBLE("double", 8), STRING(String.class.getName(), -1);
        String type;
        int    size;

        Primitive(String type, int size) {
            this.type = type;
            this.size = size;
        }

        /**
         * Read a primitive
         *
         * @param is
         * @param limit
         *         Maximum amount of bytes to read
         *
         * @return The primitive
         *
         * @throws IOException
         */
        Object read(OrderedInputStream is, int limit) throws IOException {
            switch(this) {
                case BOOLEAN:
                    return is.readBoolean();
                case BYTE:
                    return is.readByte();
                case CHAR:
                    return is.readChar();
                case SHORT:
                    return is.readShort();
                case INT:
                    return is.readInt();
                case LONG:
                    return is.readLong();
                case FLOAT:
                    return is.readFloat();
                case DOUBLE:
                    return is.readDouble();
                case STRING:
                    return is.readString(limit);
                default:
                    return null;
            }
        }
    }
}
