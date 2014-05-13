package com.timepath.swing;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
@SuppressWarnings("serial")
public class StatusBar extends JToolBar {

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    private static final Logger LOG = Logger.getLogger(StatusBar.class.getName());

    /**
     * Creates new form BlendedToolBar
     */
    public StatusBar() {
        initComponents();
    }

    private void initComponents() {
        setFloatable(false);
        setMaximumSize(new Dimension(32767, 20));
        setMinimumSize(new Dimension(2, 20));
        setPreferredSize(new Dimension(2, 20));
    }
}
