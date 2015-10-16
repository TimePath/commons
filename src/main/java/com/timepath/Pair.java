package com.timepath;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * @param <K>
 * @param <V>
 * @author TimePath
 */
public class Pair<K, V> {

    private static final String PROP_KEY = "PROP_KEY";
    private static final String PROP_VAL = "PROP_VAL";
    private static final Logger LOG = Logger.getLogger(Pair.class.getName());
    private final transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final transient VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this);
    private K key;
    private V value;

    public Pair(K key, V val) {
        this.key = key;
        value = val;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + ((key != null) ? key.hashCode() : 0);
        hash = 79 * hash + ((value != null) ? value.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @NotNull Pair<?, ?> other = (Pair<?, ?>) obj;
        if ((key != other.key) && ((key == null) || !key.equals(other.key))) {
            return false;
        }
        return !((value != other.value) && ((value == null) || !value.equals(other.value)));
    }

    @NotNull
    @Override
    public String toString() {
        return MessageFormat.format("'{'{0} = {1}'}'", key, value);
    }

    /**
     * @return the key
     */
    public K getKey() {
        return key;
    }

    /**
     * @param key the key to set
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
     * @throws java.beans.PropertyVetoException
     */
    public void setValue(V value) throws PropertyVetoException {
        V oldVal = this.value;
        vetoableChangeSupport.fireVetoableChange(PROP_VAL, oldVal, value);
        this.value = value;
        propertyChangeSupport.firePropertyChange(PROP_VAL, oldVal, value);
    }
}
