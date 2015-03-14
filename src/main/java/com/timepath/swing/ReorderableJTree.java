package com.timepath.swing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
public class ReorderableJTree extends JTree {

    private static final Logger LOG = Logger.getLogger(ReorderableJTree.class.getName());
    private static final long serialVersionUID = 1L;
    private int maxDragLevel = -1;
    private int maxDropLevel = -1;
    private int minDragLevel = -1;
    private int minDropLevel = -1;

    public ReorderableJTree() {
        setDragEnabled(true);
        setDropMode(DropMode.ON_OR_INSERT);
        setTransferHandler(new TreeTransferHandler());
    }

    public int getMaxDragLevel() {
        return maxDragLevel;
    }

    public void setMaxDragLevel(int maxDragLevel) {
        this.maxDragLevel = maxDragLevel;
    }

    public int getMaxDropLevel() {
        return maxDropLevel;
    }

    /**
     * Sets the maximum dropping level
     *
     * @param maxDropLevel
     */
    public void setMaxDropLevel(int maxDropLevel) {
        this.maxDropLevel = maxDropLevel;
    }

    public int getMinDragLevel() {
        return minDragLevel;
    }

    /**
     * Sets the minimum level of allowed movable nodes
     *
     * @param minDragLevel
     */
    public void setMinDragLevel(int minDragLevel) {
        this.minDragLevel = minDragLevel;
    }

    public int getMinDropLevel() {
        return minDropLevel;
    }

    public void setMinDropLevel(int minDropLevel) {
        this.minDropLevel = minDropLevel;
    }

    private class TreeTransferHandler extends TransferHandler {

        private static final long serialVersionUID = 1L;
        @Nullable
        private DataFlavor[] flavors;
        @Nullable
        private DataFlavor nodesFlavor;

        private TreeTransferHandler() {
            nodesFlavor = new DataFlavor(NodesTransferable.class, null);
            flavors = new DataFlavor[]{nodesFlavor};
        }

        @Override
        public boolean importData(@NotNull TransferSupport support) {
            LOG.fine("importData");
            if (!support.isDrop()) { // Clipboard paste
                return false;
            }
            if (!canImport(support)) {
                return false;
            }
            // Get drop destination info
            @NotNull JTree tree = (JTree) support.getComponent();
            @NotNull DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            @NotNull JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
            int childIndex = dl.getChildIndex();
            TreePath dest = dl.getPath();
            @NotNull MutableTreeNode parent = (MutableTreeNode) dest.getLastPathComponent();
            // Extract transfer data
            @Nullable NodesTransferable xfer = null;
            try {
                xfer = (NodesTransferable) support.getTransferable().getTransferData(nodesFlavor);
            } catch (UnsupportedFlavorException ufe) {
                LOG.log(Level.WARNING, "UnsupportedFlavor: {0}", ufe.getMessage());
            } catch (IOException ioe) {
                LOG.log(Level.WARNING, "I/O error: {0}", ioe.getMessage());
            }
            if (xfer == null) {
                return false;
            }
            @NotNull List<DefaultMutableTreeNode> nodes = xfer.getNodes();
            // Set index
            int index = childIndex;    // DropMode.INSERT
            if (childIndex == -1) {     // DropMode.ON
                index = parent.getChildCount(); // End of list
            }
            for (DefaultMutableTreeNode node : nodes) {
                model.insertNodeInto(node, parent, index++);
            }
            return true;
        }

        @Override
        public boolean canImport(@NotNull TransferSupport support) {
            try {
                if (!support.isDataFlavorSupported(nodesFlavor)) {
                    return false;
                }
            } catch (NullPointerException ignored) {
                // No data flavors
                return false;
            }
            support.setShowDropLocation(true);
            // Get drop location info
            @NotNull JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
            TreePath dest = dl.getPath();
            if (dest == null) {
                return false;
            }
            // Drop target
            @NotNull DefaultMutableTreeNode target = (DefaultMutableTreeNode) dest.getLastPathComponent();
            // Convert nodes to usable format
            List<DefaultMutableTreeNode> nodes;
            try {
                @NotNull NodesTransferable xfer = (NodesTransferable) support.getTransferable().getTransferData(nodesFlavor);
                nodes = xfer.getNodes();
            } catch (UnsupportedFlavorException ignored) {
                return false;
            } catch (IOException ignored) {
                return false;
            }
            // Level check
            if (((maxDropLevel > -1) && (target.getLevel() > maxDropLevel)) ||
                    ((minDropLevel > -1) && (target.getLevel() < minDropLevel))) {
                return false;
            }
            // Disallow MOVE-action drops if a node's children are not selected
            //            if(support.getDropAction() == MOVE && !haveCompleteNode((JTree) support.getComponent())) {
            //                return false;
            //            }
            for (@NotNull DefaultMutableTreeNode node : nodes) {
                if (((minDragLevel > -1) && (node.getLevel() < minDragLevel)) ||
                        ((maxDragLevel > -1) && (node.getLevel() > maxDragLevel))) {
                    return false;
                }
                // Do not allow a drop on the drag source's parent
                if (target == node.getParent()) {
                    return false;
                }
                // Do not allow a drop on the drag source's descendants
                if (node.isNodeDescendant(target)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            LOG.fine("createTransferable");
            @NotNull JTree tree = (JTree) c;
            @Nullable TreePath[] paths = tree.getSelectionPaths();
            if (paths == null) {
                return null;
            }
            // Make up a node array of nodes for transfer and removed in exportDone
            @NotNull List<DefaultMutableTreeNode> toMove = new LinkedList<>();
            @NotNull DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
            toMove.add(node);
            for (int i = 1; i < paths.length; i++) {
                Object o = paths[i].getLastPathComponent();
                @NotNull DefaultMutableTreeNode next = (DefaultMutableTreeNode) o;
                if (next.getLevel() == node.getLevel()) { // Siblings only
                    toMove.add(next);
                }
            }
            return new NodesTransferable(toMove);
        }

        @Override
        protected void exportDone(JComponent source, @NotNull Transferable data, int action) {
            LOG.fine("exportDone");
            if ((action & TransferHandler.MOVE) != 0) {
                @NotNull JTree tree = (JTree) source;
                @NotNull DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                @Nullable NodesTransferable xfer = null;
                try {
                    xfer = (NodesTransferable) data.getTransferData(nodesFlavor);
                } catch (UnsupportedFlavorException ufe) {
                    LOG.log(Level.WARNING, "UnsupportedFlavorException: {0}", ufe.getMessage());
                } catch (IOException ioe) {
                    LOG.log(Level.WARNING, "IOException: {0}", ioe.getMessage());
                }
                if (xfer == null) {
                    return;
                }
                model.nodeStructureChanged((TreeNode) model.getRoot());
            }
        }

        @NotNull
        @Override
        public String toString() {
            return getClass().getName();
        }

        private boolean haveCompleteNode(@NotNull JTree tree) {
            @Nullable int[] selRows = tree.getSelectionRows(); // XXX: Bad
            if ((selRows == null) || (selRows.length == 0)) {
                return true;
            }
            TreePath path = tree.getPathForRow(selRows[0]);
            @NotNull DefaultMutableTreeNode first = (DefaultMutableTreeNode) path.getLastPathComponent();
            int childCount = first.getChildCount();
            // First has children and no children are selected
            if ((childCount > 0) && (selRows.length == 1)) {
                return false;
            }
            // First may have children
            for (int i = 1; i < selRows.length; i++) {
                path = tree.getPathForRow(selRows[i]);
                @NotNull TreeNode next = (TreeNode) path.getLastPathComponent();
                if (first.isNodeChild(next)) {
                    // Found a child of first
                    if (childCount > (selRows.length - 1)) {
                        // Not all children of first are selected
                        return false;
                    }
                }
            }
            return true;
        }

        private class NodesTransferable implements Transferable {

            private final List<DefaultMutableTreeNode> nodes;

            private NodesTransferable(List<DefaultMutableTreeNode> move) {
                nodes = move;
            }

            /**
             * @return the nodes
             */
            @NotNull
            public List<DefaultMutableTreeNode> getNodes() {
                return Collections.unmodifiableList(nodes);
            }

            @NotNull
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return flavors;
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return nodesFlavor.equals(flavor);
            }

            @NotNull
            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (!isDataFlavorSupported(flavor)) {
                    throw new UnsupportedFlavorException(flavor);
                }
                return this;
            }
        }
    }
}
