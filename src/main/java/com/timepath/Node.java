package com.timepath;

import com.timepath.swing.TreeUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
public abstract class Node<A extends Pair, B extends Node<A, B>> {

    private static final Logger  LOG        = Logger.getLogger(Node.class.getName());
    protected final      List<B> children   = new ArrayList<>(0);
    protected final      List<A> properties = new ArrayList<>(0);
    protected Object custom;
    protected B      parent;

    public Node() {
        custom = toString();
    }

    @Override
    public String toString() {
        return (String) custom;
    }

    public Node(Object a) {
        custom = a;
    }

    @SafeVarargs
    public static <A extends Pair, B extends Node<A, B>> void debug(final B... l) {
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

    public static <A extends Pair, B extends Node<A, B>> void debugDiff(Diff<B> diff) {
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

    public Object getValue(Object key) {
        return getValue(key, null);
    }

    public Object getValue(Object key, Object placeholder) {
        for(A p : properties) {
            if(key.equals(p.getKey())) return p.getValue();
        }
        return placeholder;
    }

    @SafeVarargs
    public final void addAllProperties(A... properties) {
        addAllProperties(Arrays.asList(properties));
    }

    public void addAllProperties(Iterable<A> c) {
        for(A property : c) {
            addProperty(property);
        }
    }

    public void addProperty(A property) {
        properties.add(property);
    }

    /**
     * @return the properties
     */
    public List<A> getProperties() {
        return properties;
    }

    @SafeVarargs
    public final void addAllNodes(B... nodes) {
        addAllNodes(Arrays.asList(nodes));
    }

    public void addAllNodes(Iterable<B> c) {
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
    public List<B> getNodes() {
        return Collections.unmodifiableList(children);
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

    public boolean has(Object identifier) {
        return get(identifier) != null;
    }

    public B get(Object identifier) {
        for(B b : children) {
            if(b.custom.equals(identifier)) {
                return b;
            }
        }
        return null;
    }

    public B get(Object... path) {
        B result = get(path[0]);
        for(int i = 1; i < path.length; i++) {
            if(result == null) return null;
            result = result.get(path[i]);
        }
        return result;
    }

    public String printTree() {
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

    public JTree toTree() {
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

    public DefaultMutableTreeNode toTreeNode() {
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
