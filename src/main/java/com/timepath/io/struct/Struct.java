package com.timepath.io.struct;

import com.timepath.io.OrderedInputStream;
import com.timepath.io.OrderedOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Struct {

    private static final Logger LOG = Logger.getLogger(Struct.class.getName());

    private Struct() {
    }

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
    @Deprecated
    public static int sizeof(Class<?> clazz)
            throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        return sizeof(clazz.newInstance());
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
    public static int sizeof(Object instance) throws IllegalAccessException, InstantiationException {
        int size = 0;
        for (Field field : instance.getClass().getDeclaredFields()) {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            StructField meta = field.getAnnotation(StructField.class);
            if (meta != null) {
                size += sizeof(field.getType(), meta, field.get(instance));
            }
            field.setAccessible(accessible);
        }
        return size;
    }

    public static byte[] pack(Object instance) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            pack(instance, new OrderedOutputStream(baos));
            return baos.toByteArray();
        } catch (IOException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void pack(Object instance, OrderedOutputStream os)
            throws IllegalAccessException, IOException, InstantiationException {
        for (Field field : getFields(instance.getClass())) {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            writeField(instance, field, os);
            field.setAccessible(accessible);
        }
    }

    private static void writeField(Object instance, Field field, OrderedOutputStream os)
            throws IOException, IllegalAccessException, InstantiationException {
        Object ref = field.get(instance);
        StructField meta = field.getAnnotation(StructField.class);
        os.write(new byte[meta.skip()]);
        Primitive primitive = Primitive.get(field.getType());
        if (primitive != null) { // Field is a primitive type
            primitive.write(ref, os, meta.limit());
        } else if (field.getType().isArray()) { // Field is an array
            if (ref == null) { // Check if instantiated
                if (meta.nullable()) return;
                throw new InstantiationException("Cannnot instantiate array of unknown length");
            }
            writeArray(instance, field, os, 0);
        } else { // Field is a regular Object
            if (ref == null) { // Skip over
                LOG.log(Level.FINE, "Instantiating {0}", field);
                os.write(new byte[sizeof(instantiate(field.getType()))]);
            } else {
                pack(ref, os);
            }
        }
    }

    private static void writeArray(Object instance, Field field, OrderedOutputStream os, int depth)
            throws IOException, InstantiationException, IllegalAccessException {
        StructField meta = field.getAnnotation(StructField.class);
        int dimensions = getArrayDepth(field.getType());
        Class<?> elemType = getArrayType(field.getType());
        Primitive primitive = Primitive.get(elemType);
        Object ref = field.get(instance);
        for (int i = 0; i < Array.getLength(ref); i++) {
            Object elem = Array.get(ref, i);
            if (depth == dimensions) { // Not a nested array
                if (primitive != null) { // Element is a primitive type
                    primitive.write(elem, os, meta.limit());
                } else {
                    if (elem == null) { // Instantiate if needed
                        throw new UnsupportedOperationException("Null objects not yet supported");
                    }
                    pack(elem, os);
                }
                Array.set(ref, i, elem);
            } else {
                writeArray(elem, field, os, depth + 1);
            }
        }
    }

    public static void unpack(Object out, byte... b) {
        try {
            unpack(out, new OrderedInputStream(new ByteArrayInputStream(b)));
        } catch (IOException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static void unpack(Object instance, OrderedInputStream is)
            throws IOException, IllegalAccessException, InstantiationException {
        for (Field field : getFields(instance.getClass())) {
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
        } catch (Throwable t) {
            exStack.add(0, t);
        }
        try {
            Constructor<?> ctor = type.getDeclaredConstructors()[0];
            boolean accessible = ctor.isAccessible();
            ctor.setAccessible(true);
            Object instance = ctor.newInstance(new Object[0]);
            ctor.setAccessible(accessible);
            return instance;
        } catch (Throwable t) {
            exStack.add(0, t);
        }
        throw new InstantiationException(exStack.toString());
    }

    private static int getArrayDepth(Class<?> clazz) {
        return clazz.getName().lastIndexOf('[');
    }

    private static Class<?> getArrayType(Class<?> clazz) {
        Class<?> elemType = clazz;
        for (int i = 0; i < (getArrayDepth(clazz) + 1); i++) {
            elemType = elemType.getComponentType();
        }
        return elemType;
    }

    private static void readArray(Object ref, Field field, OrderedInputStream is, int depth)
            throws IOException, InstantiationException, IllegalAccessException {
        StructField meta = field.getAnnotation(StructField.class);
        int dimensions = getArrayDepth(field.getType());
        Class<?> elemType = getArrayType(field.getType());
        Primitive primitive = Primitive.get(elemType);
        for (int i = 0; i < Array.getLength(ref); i++) {
            Object elem = Array.get(ref, i);
            if (depth == dimensions) { // Not a nested array
                if (primitive != null) { // Element is a primitive type
                    elem = primitive.read(is, meta.limit());
                } else {
                    if (elem == null) { // Instantiate if needed
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
            throws IOException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        StructField meta = field.getAnnotation(StructField.class);
        is.skipBytes(meta.skip());
        Object ref;
        Primitive primitive = Primitive.get(field.getType());
        if (primitive != null) { // Field is a primitive type
            return primitive.read(is, meta.limit());
        } else if (field.getType().isArray()) { // Field is an array
            ref = field.get(instance);
            if (ref == null) { // Check if instantiated
                throw new InstantiationException("Cannnot instantiate array of unknown length");
            }
            readArray(ref, field, is, 0);
        } else { // Field is a regular Object
            ref = field.get(instance);
            if (ref == null) { // Instantiate if needed
                LOG.log(Level.FINE, "Instantiating {0}", field);
                ref = instantiate(field.getType());
            }
            unpack(ref, is);
            field.set(instance, ref);
        }
        return ref;
    }

    private static int sizeof(Class<?> type, StructField meta, Object ref)
            throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        int size = 0;
        Primitive primitive = Primitive.get(type);
        if (primitive != null) { // Field is primitive
            int sz = primitive.size;
            if (sz < 0) {
                if (meta.limit() <= 0) { // Dynamic length String
                    return Integer.MIN_VALUE;
                }
                sz = meta.limit(); // Limit string
            }
            size += ((meta.limit() > 0) ? Math.min(sz, meta.limit()) : sz) + meta.skip();
        } else if (type.isArray()) { // Field is an array
            if (ref == null) { // Check if instantiated
                throw new InstantiationException("Cannnot instantiate array of unknown length");
            }
            for (int i = 0; i < Array.getLength(ref); i++) {
                size += sizeof(type.getComponentType(), meta, Array.get(ref, i));
            }
        } else { // Field is a regular Object
            if (ref == null) { // Instantiate if needed
                LOG.log(Level.FINE, "Instantiating {0}", type);
                ref = type.newInstance();
            }
            int sz = sizeof(ref);
            size += ((meta.limit() > 0) ? Math.min(sz, meta.limit()) : sz) + meta.skip();
        }
        return size;
    }

    private static List<Field> getFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        // Filter
        List<Field> al = new LinkedList<>();
        for (Field ref : fields) {
            StructField field = ref.getAnnotation(StructField.class);
            if (field != null) {
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
        return al;
    }

    private enum Primitive {
        BYTE("byte", 1), BOOLEAN("boolean", 1),
        SHORT("short", 2), CHAR("char", 2),
        INT("int", 4), FLOAT("float", 4),
        LONG("long", 8), DOUBLE("double", 8),
        STRING(String.class, -1);
        private static final Map<String, Primitive> primitiveTypes;

        static {
            Primitive[] values = values();
            primitiveTypes = new HashMap<>(values.length);
            for (Primitive p : values) primitiveTypes.put(p.type, p);
        }

        String type;
        int size;

        Primitive(String type, int size) {
            this.type = type;
            this.size = size;
        }

        Primitive(Class<?> type, int size) {
            this.type = type.getName();
            this.size = size;
        }

        public static Primitive get(final Class<?> type) {
            return primitiveTypes.get(type.getName());
        }

        /**
         * Read a primitive
         *
         * @param is
         * @param limit Maximum amount of bytes to read
         * @return The primitive
         * @throws IOException
         */
        Object read(OrderedInputStream is, int limit) throws IOException {
            switch (this) {
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
                    if (limit > 0) { // Fixed size
                        byte[] b = new byte[limit];
                        is.readFully(b);
                        return new String(b, StandardCharsets.UTF_8);
                    }
                    return is.readString(limit); // NUL terminated
                default:
                    return null;
            }
        }

        public void write(Object o, OrderedOutputStream os, int limit) throws IOException {
            switch (this) {
                case BOOLEAN:
                    os.writeBoolean((boolean) o);
                    break;
                case BYTE:
                    os.writeByte((byte) o);
                    break;
                case CHAR:
                    os.writeChar((char) o);
                    break;
                case SHORT:
                    os.writeShort((short) o);
                    break;
                case INT:
                    os.writeInt((int) o);
                    break;
                case LONG:
                    os.writeLong((long) o);
                    break;
                case FLOAT:
                    os.writeFloat((float) o);
                    break;
                case DOUBLE:
                    os.writeDouble((double) o);
                    break;
                case STRING:
                    String s = (String) o;
                    byte[] b = s.getBytes(StandardCharsets.UTF_8);
                    if (limit > 0) { // Fixed size
                        int min = Math.min(limit, b.length);
                        os.write(b, 0, min);
                        if (min == b.length) os.write(new byte[limit - b.length]); // Padding
                    } else {
                        os.write(b, 0, b.length);
                        os.write(0); // NUL
                    }
                    break;
            }
        }
    }
}
