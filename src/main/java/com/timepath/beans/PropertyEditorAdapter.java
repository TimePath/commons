package com.timepath.beans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;

public class PropertyEditorAdapter implements PropertyEditor {

    public static final String[] NO_TAGS = new String[0];

    public PropertyEditorAdapter() {
    }

    @Nullable
    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setValue(Object value) {
    }

    @Override
    public boolean isPaintable() {
        return false;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
    }

    @NotNull
    @Override
    public String getJavaInitializationString() {
        return null;
    }

    @Nullable
    @Override
    public String getAsText() {
        return null;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
    }

    @NotNull
    @Override
    public String[] getTags() {
        return NO_TAGS;
    }

    @Nullable
    @Override
    public Component getCustomEditor() {
        return null;
    }

    @Override
    public boolean supportsCustomEditor() {
        return false;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    }
}
