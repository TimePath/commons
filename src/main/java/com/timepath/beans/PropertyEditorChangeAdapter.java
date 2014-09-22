package com.timepath.beans;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PropertyEditorChangeAdapter extends PropertyEditorAdapter {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public PropertyEditorChangeAdapter() {
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pl) {
        propertyChangeSupport.addPropertyChangeListener(pl);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pl) {
        propertyChangeSupport.removePropertyChangeListener(pl);
    }

    protected void firePropertyChange(Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(null, oldValue, newValue);
    }
}
