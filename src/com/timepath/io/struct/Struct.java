package com.timepath.io.struct;

import com.timepath.io.OrderedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
        }
    }

    public static void unpack(Object out, OrderedInputStream is) {
        Field[] fields = out.getClass().getDeclaredFields();
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
        try {
            for(Field field : al) {
                StructField meta = field.getAnnotation(StructField.class);
                is.skipBytes(meta.skip());
                Object var;
                Primitive primitive = primitiveTypes.get(field.getType().getName());
                if(primitive == null) {
                    primitive = Primitive.OBJECT;
                }
                switch(primitive) {
                    case BOOLEAN:
                        var = is.readBoolean();
                        break;
                    case BYTE:
                        var = is.readByte();
                        break;
                    case CHAR:
                        var = is.readChar();
                        break;
                    case SHORT:
                        var = is.readShort();
                        break;
                    case INT:
                        var = is.readInt();
                        break;
                    case LONG:
                        var = is.readLong();
                        break;
                    case FLOAT:
                        var = is.readFloat();
                        break;
                    case DOUBLE:
                        var = is.readDouble();
                        break;
                    case STRING:
                        var = is.readString(meta.limit());
                        break;
                    case OBJECT:
                        LOG.log(Level.FINE, "Instantiating {0}", field);
                        var = field.getType().newInstance(); // Fatal if fails
                        Struct.unpack(var, is);
                        break;
                    default:
                        continue;
                }
                try {
                    field.setAccessible(true);
                    field.set(out, var);
                } catch(IllegalArgumentException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch(IllegalAccessException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }

            }
        } catch(IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch(InstantiationException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch(IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
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

    }

}
