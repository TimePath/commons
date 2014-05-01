package com.timepath.swing;

import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author TimePath
 */
public class TreeUtils {

    public static void expand(JTree tree) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        Enumeration e = root.breadthFirstEnumeration();
        while(e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if(node.isLeaf()) {
                continue;
            }
            int row = tree.getRowForPath(new TreePath(node.getPath()));
            tree.expandRow(row);
        }
    }

    public static void moveChildren(DefaultMutableTreeNode source, DefaultMutableTreeNode dest) {
        Enumeration<DefaultMutableTreeNode> e = source.children();
        while(e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) source.getChildAt(0); // FIXME: e.nextElement() doesn't work. Why?
            node.removeFromParent();
            dest.add(node);
        }
    }

    private TreeUtils() {
    }

}
