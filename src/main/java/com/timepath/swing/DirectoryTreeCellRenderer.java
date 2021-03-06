package com.timepath.swing;

import com.timepath.io.utils.ViewableData;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
@SuppressWarnings("serial")
public class DirectoryTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final Logger LOG = Logger.getLogger(DirectoryTreeCellRenderer.class.getName());

    public DirectoryTreeCellRenderer() {
    }

    @NotNull
    @Override
    public Component getTreeCellRendererComponent(@NotNull JTree tree,
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus) {
        @NotNull Component comp = super.getTreeCellRendererComponent(tree, value, sel, sel, false, row, hasFocus);
        if (comp instanceof JLabel) {
            @NotNull JLabel label = (JLabel) comp;
            if (value instanceof DefaultMutableTreeNode) {
                @NotNull DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) value;
                if (dmtn.getUserObject() instanceof ViewableData) {
                    @NotNull ViewableData data = (ViewableData) dmtn.getUserObject();
                    label.setIcon(null);
                    label.setIcon(data.getIcon());
                    label.setText(data.toString());
                    return label;
                }
            }
        }
        return comp;
    }
}
