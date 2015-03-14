package com.timepath.beans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
public class BeanEditor extends JPanel {

    private static final Logger LOG = Logger.getLogger(BeanEditor.class.getName());
    private Object bean;
    private JEditorPane jEditorPane1;
    private JTable jTable1;

    public BeanEditor() {
        initComponents();
        jTable1.getColumnModel().getColumn(2).setCellRenderer(new ComponentCell());
        jTable1.getColumnModel().getColumn(2).setCellEditor(new ComponentCell());
        jTable1.setRowHeight(30);
    }

    private void initComponents() {
        @NotNull JSplitPane jSplitPane1 = new JSplitPane();
        @NotNull JScrollPane jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();
        @NotNull JScrollPane jScrollPane2 = new JScrollPane();
        jEditorPane1 = new JEditorPane();
        jSplitPane1.setDividerLocation(-1);
        jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);
        jTable1.setModel(new DefaultTableModel(new Object[][]{
        }, new String[]{
                "Key", "Value", ""
        }
        ) {
            @NotNull
            boolean[] canEdit = {
                    false, true, true
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jTable1);
        jSplitPane1.setTopComponent(jScrollPane1);
        jEditorPane1.setEditable(false);
        jScrollPane2.setViewportView(jEditorPane1);
        jSplitPane1.setBottomComponent(jScrollPane2);
        @NotNull GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
        );
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object o) {
        try {
            bean = o;
            @NotNull BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            @NotNull Method objectClass = Object.class.getDeclaredMethod("getClass", (Class<?>[]) null);
            for (@NotNull PropertyDescriptor p : info.getPropertyDescriptors()) {
                if (p.getReadMethod().equals(objectClass)) {
                    continue;
                }
                jEditorPane1.setText(p.getShortDescription());
                Method read = p.getReadMethod();
                final PropertyEditor editor = p.createPropertyEditor(bean);
                Object value = read.invoke(bean, (Object[]) null);
                @Nullable JButton jb = null;
                if (editor != null) {
                    editor.setValue(value);
                    editor.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(@NotNull PropertyChangeEvent pce) {
                            LOG.log(Level.FINE, null, pce.getNewValue());
                        }
                    });
                    value = editor.getAsText();
                    if (editor.supportsCustomEditor()) {
                        jb = new JButton("...");
                        jb.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent ae) {
                                @NotNull JFrame f = new JFrame();
                                f.add(editor.getCustomEditor());
                                f.pack();
                                f.setLocationRelativeTo(null);
                                f.setVisible(true);
                            }
                        });
                    }
                }
                @NotNull Object[] data = {p.getName(), value, jb};
                ((DefaultTableModel) jTable1.getModel()).addRow(data);
            }
        } catch (@NotNull IntrospectionException | InvocationTargetException | IllegalArgumentException | IllegalAccessException |
                SecurityException | NoSuchMethodException ex) {
            Logger.getLogger(BeanEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class ComponentCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

        JPanel panel;
        JButton showButton;
        Component feed;

        ComponentCell() {
            showButton = new JButton("View Articles");
            showButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    JOptionPane.showMessageDialog(null, "Reading " + feed.getName());
                }
            });
            panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(showButton);
        }

        @Override
        public Component getTableCellEditorComponent(@NotNull JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof Component) {
                @NotNull Component c = (Component) value;
                updateData(c, true, table);
                return c;
            }
            return panel;
        }

        private void updateData(Component feed, boolean isSelected, @NotNull JTable table) {
            this.feed = feed;
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getSelectionForeground());
            }
        }

        @Nullable
        @Override
        public Object getCellEditorValue() {
            return null;
        }

        @NotNull
        @Override
        public Component getTableCellRendererComponent(@NotNull JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            if (value instanceof Component) {
                @NotNull Component c = (Component) value;
                updateData(c, isSelected, table);
                return c;
            }
            return panel;
        }
    }
}
