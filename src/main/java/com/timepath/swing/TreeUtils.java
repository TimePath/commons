package com.timepath.swing;

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

    public static void expand(JTree tree) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        Enumeration e = root.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.isLeaf()) {
                continue;
            }
            int row = tree.getRowForPath(new TreePath(node.getPath()));
            tree.expandRow(row);
        }
    }

    public static void moveChildren(TreeNode source, DefaultMutableTreeNode dest) {
        Enumeration<DefaultMutableTreeNode> e = source.children();
        while (e.hasMoreElements()) {
            MutableTreeNode node = (MutableTreeNode) source.getChildAt(0); // FIXME: e.nextElement() doesn't work. Why?
            node.removeFromParent();
            dest.add(node);
        }
    }
}
