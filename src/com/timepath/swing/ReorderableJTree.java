package com.timepath.swing;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.*;

/**
 *
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
     * <p/>
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
     * <p/>
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

        private DataFlavor[] flavors;

        private DataFlavor nodesFlavor;

        private TreeTransferHandler() {
            try {
                String mimeType = MessageFormat.format("{0};class=\"{1}\"",
                                                       DataFlavor.javaJVMLocalObjectMimeType,
                                                       NodesTransferable.class.getName());
                nodesFlavor = new DataFlavor(mimeType);
                flavors = new DataFlavor[] {nodesFlavor};
            } catch(ClassNotFoundException cnfe) {
                LOG.log(Level.SEVERE, "ClassNotFoundException: {0}", cnfe.getMessage());
            }
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            if(!support.isDataFlavorSupported(nodesFlavor)) {
                return false;
            }

            support.setShowDropLocation(true);

            // Get drop location info
            JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
            TreePath dest = dl.getPath();
            if(dest == null) {
                return false;
            }

            // Drop target
            DefaultMutableTreeNode target = (DefaultMutableTreeNode) dest.getLastPathComponent();

            // Convert nodes to usable format
            List<DefaultMutableTreeNode> nodes;
            try {
                NodesTransferable xfer = (NodesTransferable) support.getTransferable()
                    .getTransferData(nodesFlavor);
                nodes = xfer.getNodes();
            } catch(UnsupportedFlavorException ufe) {
                return false;
            } catch(IOException ioe) {
                return false;
            }

            // Level check
            if((maxDropLevel > -1 && target.getLevel() > maxDropLevel)
                   || (minDropLevel > -1 && target.getLevel() < minDropLevel)) {
                return false;
            }

            // Disallow MOVE-action drops if a node's children are not selected
            if(support.getDropAction() == MOVE && !haveCompleteNode((JTree) support.getComponent())) {
//                return false;
            }

            for(DefaultMutableTreeNode node : nodes) {
                if((minDragLevel > -1 && node.getLevel() < minDragLevel)
                       || (maxDragLevel > -1 && node.getLevel() > maxDragLevel)) {
                    return false;
                }

                // Do not allow a drop on the drag source's parent
                if(target == node.getParent()) {
                    return false;
                }

                // Do not allow a drop on the drag source's descendants
                if(node.isNodeDescendant(target)) {
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
        public boolean importData(TransferHandler.TransferSupport support) {
            LOG.fine("importData");
            if(!support.isDrop()) { // Clipboard paste
                return false;
            }
            if(!canImport(support)) {
                return false;
            }

            // Get drop destination info
            JTree tree = (JTree) support.getComponent();
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
            int childIndex = dl.getChildIndex();
            TreePath dest = dl.getPath();
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) dest.getLastPathComponent();

            // Extract transfer data
            NodesTransferable xfer = null;
            try {
                xfer = (NodesTransferable) support.getTransferable().getTransferData(
                    nodesFlavor);
            } catch(UnsupportedFlavorException ufe) {
                LOG.log(Level.WARNING, "UnsupportedFlavor: {0}", ufe.getMessage());
            } catch(java.io.IOException ioe) {
                LOG.log(Level.WARNING, "I/O error: {0}", ioe.getMessage());
            }
            if(xfer == null) {
                return false;
            }
            List<DefaultMutableTreeNode> nodes = xfer.getNodes();
            // Set index
            int index = childIndex;    // DropMode.INSERT
            if(childIndex == -1) {     // DropMode.ON
                index = parent.getChildCount(); // End of list
            }

            for(DefaultMutableTreeNode node : nodes) {
                model.insertNodeInto(node, parent, index++);
            }
            return true;
        }

        @Override
        public String toString() {
            return getClass().getName();
        }

        private boolean haveCompleteNode(JTree tree) {
            int[] selRows = tree.getSelectionRows(); // XXX: Bad
            if(selRows == null || selRows.length == 0) {
                return true;
            }
            TreePath path = tree.getPathForRow(selRows[0]);
            DefaultMutableTreeNode first = (DefaultMutableTreeNode) path.getLastPathComponent();
            int childCount = first.getChildCount();
            // First has children and no children are selected
            if(childCount > 0 && selRows.length == 1) {
                return false;
            }
            // First may have children
            for(int i = 1; i < selRows.length; i++) {
                path = tree.getPathForRow(selRows[i]);
                DefaultMutableTreeNode next = (DefaultMutableTreeNode) path.getLastPathComponent();
                if(first.isNodeChild(next)) {
                    // Found a child of first
                    if(childCount > selRows.length - 1) {
                        // Not all children of first are selected
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            LOG.fine("createTransferable");
            JTree tree = (JTree) c;
            TreePath[] paths = tree.getSelectionPaths();
            if(paths == null) {
                return null;
            }
            // Make up a node array of nodes for transfer and removed in exportDone
            List<DefaultMutableTreeNode> toMove = new LinkedList<DefaultMutableTreeNode>();

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
            toMove.add(node);
            for(int i = 1; i < paths.length; i++) {
                Object component = paths[i].getLastPathComponent();
                DefaultMutableTreeNode next = (DefaultMutableTreeNode) component;

                if(next.getLevel() == node.getLevel()) { // Siblings only
                    toMove.add(next);
                }
            }
            return new NodesTransferable(toMove);
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            LOG.fine("exportDone");
            if((action & MOVE) != 0) {
                JTree tree = (JTree) source;
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                NodesTransferable xfer = null;
                try {
                    xfer = (NodesTransferable) data.getTransferData(nodesFlavor);
                } catch(UnsupportedFlavorException ufe) {
                    LOG.log(Level.WARNING, "UnsupportedFlavorException: {0}", ufe.getMessage());
                } catch(java.io.IOException ioe) {
                    LOG.log(Level.WARNING, "IOException: {0}", ioe.getMessage());
                }
                if(xfer == null) {
                    return;
                }
                model.nodeStructureChanged((TreeNode) model.getRoot());
            }
        }

        private class NodesTransferable implements Transferable {

            private final List<DefaultMutableTreeNode> nodes;

            private NodesTransferable(List<DefaultMutableTreeNode> move) {
                this.nodes = move;
            }

            /**
             * @return the nodes
             */
            public List<DefaultMutableTreeNode> getNodes() {
                return nodes;
            }

            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if(!isDataFlavorSupported(flavor)) {
                    throw new UnsupportedFlavorException(flavor);
                }
                return this;
            }

            public DataFlavor[] getTransferDataFlavors() {
                return flavors;
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return nodesFlavor.equals(flavor);
            }

        }

    }

}
