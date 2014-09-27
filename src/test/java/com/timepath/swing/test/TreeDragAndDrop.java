package com.timepath.swing.test;

import com.timepath.swing.ReorderableJTree;
import com.timepath.swing.TreeUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.util.logging.Logger;

public class TreeDragAndDrop {

    private static final Logger LOG = Logger.getLogger(TreeDragAndDrop.class.getName());

    public static void main(String[] args) {
        @NotNull JFrame f = new JFrame("Draggable Tree Test");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        @NotNull JTree tree = new ReorderableJTree();
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        @NotNull JTree tree2 = new ReorderableJTree();
        tree2.setModel(tree.getModel());
        tree2.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        TreeUtils.expand(tree);
        TreeUtils.expand(tree2);
        @NotNull JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, new JScrollPane(tree), new JScrollPane(tree2));
        sp.setDividerLocation(0.5);
        f.add(sp);
        f.setSize(400, 400);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
