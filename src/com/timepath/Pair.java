package com.timepath;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;
import java.util.logging.Logger;

/**
 *
 * @author TimePath
 * @param <K>
 * @param <V>
 */
public class Pair<K, V> {

    public static final String PROP_KEY = "PROP_KEY";

    public static final String PROP_VAL = "PROP_VAL";

    private K key;

    private final transient PropertyChangeSupport propertyChangeSupport
                                                      = new java.beans.PropertyChangeSupport(this);

    private V value;

    private final transient VetoableChangeSupport vetoableChangeSupport
                                                      = new java.beans.VetoableChangeSupport(this);

    public Pair(K key, V val) {
        this.key = key;
        this.value = val;
    }

    /**
     * @return the key
     */
    public K getKey() {
        return key;
    }

    /**
     * @param key the key to set
     * <p>
     * @throws java.beans.PropertyVetoException
     */
    public void setKey(K key) throws PropertyVetoException {
        K oldKey = this.key;
        vetoableChangeSupport.fireVetoableChange(PROP_KEY, oldKey, key);
        this.key = key;
        propertyChangeSupport.firePropertyChange(PROP_KEY, oldKey, key);
    }

    /**
     * @return the value
     */
    public V getValue() {
        return value;
    }

    /**
     * @param value the value to set
     * <p>
     * @throws java.beans.PropertyVetoException
     */
    public void setValue(V value) throws PropertyVetoException {
        V oldVal = this.value;
        vetoableChangeSupport.fireVetoableChange(PROP_VAL, oldVal, value);
        this.value = value;
        propertyChangeSupport.firePropertyChange(PROP_VAL, oldVal, value);
    }

    @Override
    public String toString() {
        return "{" + getKey() + " = " + getValue() + "}";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 79 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        if(this.key != other.key && (this.key == null || !this.key.equals(other.key))) {
            return false;
        }
        if(this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        return true;
    }

    private static final Logger LOG = Logger.getLogger(Pair.class.getName());

}
