package com.timepath;

import com.timepath.swing.TreeUtils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author TimePath
 * @param <A> Property type
 * @param <B> Your subclass
 */
public abstract class Node<A, B extends Node<A, B>> {

    private static final Logger LOG = Logger.getLogger(Node.class.getName());
    
    public static <A, B extends Node<A, B>> void debug(final B... l) {
        @SuppressWarnings("serial")
        JFrame f = new JFrame("Diff") {
            {
                this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                this.add(new JPanel() {
                    {
                        this.setLayout(new BorderLayout());
                        this.add(new JPanel() {
                            {
                                for(B n : l) {
                                    this.add(new JScrollPane(n.toTree()));
                                }
                            }
                        });
                    }
                });
                this.pack();
                this.setLocationRelativeTo(null);
            }
        };
        f.setVisible(true);
    }
    
    public static <A, B extends Node<A, B>> void debugDiff(final Diff<B> diff) {
        final B n1 = diff.in, n2 = diff.out;

        LOG.log(Level.FINE, "N1:\n{0}", n1.printTree());
        LOG.log(Level.FINE, "N2:\n{0}", n2.printTree());

        LOG.log(Level.FINE, "Deleted:\n{0}", diff.removed);
        LOG.log(Level.FINE, "New:\n{0}", diff.added);
        LOG.log(Level.FINE, "Modified:\n{0}", diff.modified);
        LOG.log(Level.FINE, "Same:\n{0}", diff.same);

        debug(n1, n2, diff.same.get(0), diff.removed.get(0), diff.added.get(0)); // diff.modified.get(0)
    }

    public Object custom;

    private List<B> children = new ArrayList<B>(0);

    private List<A> properties = new ArrayList<A>(0);

    private A value;

    protected B parent;

    public Node() {
        this.custom = this.toString();
    }

    public Node(Object a) {
        this.custom = a;
    }

    public void addProperty(A property) {
        this.getProperties().add(property);
    }

    public void addAllProperties(Collection<A> c) {
        for(A property : c) {
            addProperty(property);
        }
    }

    public void addAllProperties(A... properties) {
        addAllProperties(Arrays.asList(properties));
    }

    public void addAllNodes(Collection<B> c) {
        for(B n : c) {
            addNode(n);
        }
    }

    public void addAllNodes(B... nodes) {
        addAllNodes(Arrays.asList(nodes));
    }

    @SuppressWarnings("unchecked")
    public void addNode(B e) {
        e.parent = (B) this;
        getNodes().add(e);
    }

    /**
     * @return the children
     */
    public List<B> getNodes() {
        return children;
    }

    public Object getCustom() {
        return custom;
    }

    public B getNamedNode(Object identifier) {
        for(B c : this.getNodes()) {
            if(c.custom.equals(identifier)) {
                return c;
            }
        }
        return null;
    }

    /**
     * @return the parent
     */
    public B getParent() {
        return parent;
    }

    /**
     * @return the properties
     */
    public List<A> getProperties() {
        return properties;
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

    public String printTree() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(custom).append("\" {\n");
        for(A p : this.getProperties()) {
            sb.append("\t").append(p).append("\n");
        }
        StringBuilder csb = new StringBuilder();
        if(!this.getNodes().isEmpty()) {
            for(B c : this.getNodes()) {
                csb.append("\n\t").append(c.printTree().replace("\n", "\n\t")).append("\n");
            }
            sb.append(csb.substring(1));
        }
        sb.append("}");
        return sb.toString();
    }

    public abstract Diff<B> rdiff(B other);

    public void removeNode(B e) {
        e.parent = null;
        getNodes().remove(e);
    }

    @Override
    public String toString() {
        return (String) custom;
    }

    public JTree toTree() {
        JTree t = new JTree(this.toTreeNode());
        TreeUtils.expand(t);
        TreeCellRenderer tcr = new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                          boolean expanded, boolean leaf, int row,
                                                          boolean hasFocus) {
                boolean isLeaf = leaf;
                if(value instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) value;
                    if(dmtn.getUserObject() instanceof Node) {
                        isLeaf = false;
                    }
                }
                return super.getTreeCellRendererComponent(tree, value, sel, expanded, isLeaf, row,
                                                          hasFocus);
            }

        };
        t.setCellRenderer(tcr);
        return t;
    }

    public DefaultMutableTreeNode toTreeNode() {
        DefaultMutableTreeNode tn = new DefaultMutableTreeNode(this);
        for(B child : this.getNodes()) {
            tn.add(child.toTreeNode());
        }
        for(A prop : this.getProperties()) {
            tn.add(new DefaultMutableTreeNode(prop));
        }
        return tn;
    }

}
