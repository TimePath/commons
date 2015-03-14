package com.timepath.swing;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
public class TreeUtils {

    private static final Logger LOG = Logger.getLogger(TreeUtils.class.getName());

    private TreeUtils() {
    }

    public static void expand(@NotNull JTree tree) {
        @NotNull DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        @NotNull Enumeration e = root.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            @NotNull DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.isLeaf()) {
                continue;
            }
            int row = tree.getRowForPath(new TreePath(node.getPath()));
            tree.expandRow(row);
        }
    }

    public static void moveChildren(@NotNull TreeNode source, @NotNull DefaultMutableTreeNode dest) {
        Enumeration<DefaultMutableTreeNode> e = source.children();
        while (e.hasMoreElements()) {
            @NotNull MutableTreeNode node = (MutableTreeNode) source.getChildAt(0); // FIXME: e.nextElement() doesn't work. Why?
            node.removeFromParent();
            dest.add(node);
        }
    }
}
