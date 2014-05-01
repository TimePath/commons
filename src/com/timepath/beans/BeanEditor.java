package com.timepath.beans;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author TimePath
 */
public class BeanEditor extends JPanel {

    public BeanEditor() {
        initComponents();
        jTable1.getColumnModel().getColumn(2).setCellRenderer(new ComponentCell());
        jTable1.getColumnModel().getColumn(2).setCellEditor(new ComponentCell());
        jTable1.setRowHeight(30);
    }

    private Object bean;

    public void setBean(Object o) {
        try {
            this.bean = o;
            BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            Method objectClass = Object.class.getDeclaredMethod("getClass", (Class<?>[]) null);
            for(PropertyDescriptor p : info.getPropertyDescriptors()) {
                if(p.getReadMethod().equals(objectClass)) {
                    continue;
                }
                this.jEditorPane1.setText(p.getShortDescription());
                Method read = p.getReadMethod();
                final PropertyEditor editor = p.createPropertyEditor(bean);
                Object value = read.invoke(bean, (Object[]) null);
                JButton jb = null;
                if(editor != null) {
                    editor.setValue(value);
                    editor.addPropertyChangeListener(new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent pce) {
                            System.out.println(pce.getNewValue());
                        }
                    });
                    value = editor.getAsText();
                    if(editor.supportsCustomEditor()) {
                        jb = new JButton("...");
                        jb.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent ae) {
                                JFrame f = new JFrame();
                                f.add(editor.getCustomEditor());
                                f.pack();
                                f.setLocationRelativeTo(null);
                                f.setVisible(true);
                            }
                        });
                    }
                }
                Object[] data = new Object[] {p.getName(), value, jb};
                ((DefaultTableModel) this.jTable1.getModel()).addRow(data);
            }
        } catch(IntrospectionException ex) {
            Logger.getLogger(BeanEditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch(NoSuchMethodException ex) {
            Logger.getLogger(BeanEditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch(SecurityException ex) {
            Logger.getLogger(BeanEditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch(IllegalAccessException ex) {
            Logger.getLogger(BeanEditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch(IllegalArgumentException ex) {
            Logger.getLogger(BeanEditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch(InvocationTargetException ex) {
            Logger.getLogger(BeanEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Object getBean() {
        return bean;
    }

    class ComponentCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

        JPanel panel;

        JButton showButton;

        Component feed;

        public ComponentCell() {

            showButton = new JButton("View Articles");
            showButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    JOptionPane.showMessageDialog(null, "Reading " + feed.getName());
                }
            });

            panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(showButton);
        }

        private void updateData(Component feed, boolean isSelected, JTable table) {
            this.feed = feed;
            if(isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getSelectionForeground());
            }
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                     int row, int column) {
            if(value instanceof Component) {
                Component c = (Component) value;
                updateData(c, true, table);
                return c;
            }
            return panel;
        }

        public Object getCellEditorValue() {
            return null;
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row,
                                                       int column) {
            if(value instanceof Component) {
                Component c = (Component) value;
                updateData(c, isSelected, table);
                return c;
            }
            return panel;
        }

    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();

        jSplitPane1.setDividerLocation(-1);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Key", "Value", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jTable1);

        jSplitPane1.setTopComponent(jScrollPane1);

        jEditorPane1.setEditable(false);
        jScrollPane2.setViewportView(jEditorPane1);

        jSplitPane1.setBottomComponent(jScrollPane2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    private static final Logger LOG = Logger.getLogger(BeanEditor.class.getName());

}
