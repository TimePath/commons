package com.timepath;

import com.timepath.swing.TreeUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @param <A>
 *         Property type
 * @param <B>
 *         Your subclass
 *
 * @author TimePath
 */
public abstract class Node<A, B extends Node<A, B>> {

    private static final Logger        LOG        = Logger.getLogger(Node.class.getName());
    private final        Collection<B> children   = new ArrayList<>(0);
    private final        List<A>       properties = new ArrayList<>(0);
    protected Object custom;
    protected B      parent;
    private   A      value;

    protected Node() {
        custom = toString();
    }

    @Override
    public String toString() {
        return (String) custom;
    }

    protected Node(Object a) {
        custom = a;
    }

    protected static <A, B extends Node<A, B>> void debug(final B... l) {
        @SuppressWarnings("serial") JFrame frame = new JFrame("Diff") {
            {
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                add(new JPanel() {
                    {
                        setLayout(new BorderLayout());
                        add(new JPanel() {
                            {
                                for(B n : l) {
                                    add(new JScrollPane(n.toTree()));
                                }
                            }
                        });
                    }
                });
                pack();
                setLocationRelativeTo(null);
            }
        };
        frame.setVisible(true);
    }

    public static <A, B extends Node<A, B>> void debugDiff(Diff<B> diff) {
        B n1 = diff.in;
        B n2 = diff.out;
        LOG.log(Level.FINE, "N1:\n{0}", n1.printTree());
        LOG.log(Level.FINE, "N2:\n{0}", n2.printTree());
        LOG.log(Level.FINE, "Deleted:\n{0}", diff.removed);
        LOG.log(Level.FINE, "New:\n{0}", diff.added);
        LOG.log(Level.FINE, "Modified:\n{0}", diff.modified);
        LOG.log(Level.FINE, "Same:\n{0}", diff.same);
        debug(n1, n2, diff.same.get(0), diff.removed.get(0), diff.added.get(0)); // diff.modified.get(0)
    }

    public void addAllProperties(A... properties) {
        addAllProperties(Arrays.asList(properties));
    }

    void addAllProperties(Iterable<A> c) {
        for(A property : c) {
            addProperty(property);
        }
    }

    protected void addProperty(A property) {
        properties.add(property);
    }

    /**
     * @return the properties
     */
    protected List<A> getProperties() {
        return properties;
    }

    public void addAllNodes(B... nodes) {
        addAllNodes(Arrays.asList(nodes));
    }

    void addAllNodes(Iterable<B> c) {
        for(B n : c) {
            addNode(n);
        }
    }

    @SuppressWarnings("unchecked")
    public void addNode(B e) {
        e.parent = (B) this;
        children.add(e);
    }

    /**
     * @return the children
     */
    protected Iterable<B> getNodes() {
        return Collections.unmodifiableCollection(children);
    }

    public Object getCustom() {
        return custom;
    }

    /**
     * @return the parent
     */
    public B getParent() {
        return parent;
    }

    /**
     * @return the value
     */
    public A getValue() {
        return value;
    }

    public boolean has(Object identifier) {
        return getNamedNode(identifier) != null;
    }

    protected B getNamedNode(Object identifier) {
        for(B b : children) {
            if(b.custom.equals(identifier)) {
                return b;
            }
        }
        return null;
    }

    String printTree() {
        StringBuilder sb = new StringBuilder();
        sb.append('"').append(custom).append("\" {\n");
        for(A p : properties) {
            sb.append('\t').append(p).append('\n');
        }
        StringBuilder csb = new StringBuilder();
        if(!children.isEmpty()) {
            for(B c : children) {
                csb.append("\n\t").append(c.printTree().replace("\n", "\n\t")).append('\n');
            }
            sb.append(csb.substring(1));
        }
        sb.append('}');
        return sb.toString();
    }

    public abstract Diff<B> rdiff(B other);

    public void removeNode(B e) {
        e.parent = null;
        children.remove(e);
    }

    JTree toTree() {
        JTree t = new JTree(toTreeNode());
        TreeUtils.expand(t);
        TreeCellRenderer tcr = new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                                                          Object value,
                                                          boolean sel,
                                                          boolean expanded,
                                                          boolean leaf,
                                                          int row,
                                                          boolean hasFocus)
            {
                boolean isLeaf = leaf;
                if(value instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) value;
                    if(dmtn.getUserObject() instanceof Node) {
                        isLeaf = false;
                    }
                }
                return super.getTreeCellRendererComponent(tree, value, sel, expanded, isLeaf, row, hasFocus);
            }
        };
        t.setCellRenderer(tcr);
        return t;
    }

    DefaultMutableTreeNode toTreeNode() {
        DefaultMutableTreeNode tn = new DefaultMutableTreeNode(this);
        for(B child : children) {
            tn.add(child.toTreeNode());
        }
        for(A prop : properties) {
            tn.add(new DefaultMutableTreeNode(prop));
        }
        return tn;
    }
}
