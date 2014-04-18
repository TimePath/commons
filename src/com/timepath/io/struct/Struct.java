package com.timepath.io.struct;

import com.timepath.io.OrderedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TimePath
 */
public class Struct {

    private static final Logger LOG = Logger.getLogger(Struct.class.getName());

    private static final Map<String, Primitive> primitiveTypes;

    static {
        Primitive[] values = Primitive.values();

        primitiveTypes = new HashMap<String, Primitive>(values.length);
        for(Primitive p : values) {
            primitiveTypes.put(p.type, p);
        }
    }

    public static void unpack(Object out, byte[] b) {
        try {
            unpack(out, new OrderedInputStream(new ByteArrayInputStream(b)));
        } catch(IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch(IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch(InstantiationException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static void unpack(Object instance, OrderedInputStream is)
        throws IOException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        Field[] fields = instance.getClass().getDeclaredFields();
        // Filter
        ArrayList<Field> al = new ArrayList<Field>(fields.length);
        for(Field ref : fields) {
            StructField field = ref.getAnnotation(StructField.class);
            if(field != null) {
                al.add(ref);
            }
        }
        // Sort
        Collections.sort(al, new Comparator<Field>() {
            public int compare(Field o1, Field o2) {
                return o1.getAnnotation(StructField.class).index()
                           - o2.getAnnotation(StructField.class).index();
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

    private static void readArray(Object ref, Field field, OrderedInputStream is, int depth)
        throws IOException, InstantiationException, IllegalAccessException {
        StructField meta = field.getAnnotation(StructField.class);
        int dimensions = field.getType().getName().lastIndexOf('[');
        Class<?> elemType = field.getType();
        for(int i = 0; i < dimensions + 1; i++) {
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
                        elem = elemType.newInstance();
                    }
                    Struct.unpack(elem, is);
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
                ref = field.getType().newInstance();
            }
            Struct.unpack(ref, is);
            field.set(instance, ref);
        }
        return ref;
    }

    private Struct() {
    }

    private static enum Primitive {

        BOOLEAN("boolean"), BYTE("byte"), CHAR("char"), SHORT("short"), INT("int"), LONG("long"),
        FLOAT("float"), DOUBLE("double"), STRING(String.class.getName()), OBJECT("object");

        String type;

        private Primitive(String type) {
            this.type = type;
        }

        /**
         * Read a primitive
         * <p>
         * @param is
         * @param limit
         *              <p>
         * @return The primitive
         * <p>
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
