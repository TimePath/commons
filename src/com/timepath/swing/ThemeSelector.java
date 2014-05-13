package com.timepath.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
@SuppressWarnings("serial")
class ThemeSelector extends JComboBox/*<String>*/ {

    private static final Logger LOG = Logger.getLogger(ThemeSelector.class.getName());

    private ThemeSelector() {
        Vector<String> comboBoxItems = new Vector<>(0);
        DefaultComboBoxModel/*<String>*/ model = new DefaultComboBoxModel/*<String>*/(comboBoxItems);
        setModel(model);
        String lafId = UIManager.getLookAndFeel().getClass().getName();
        for(UIManager.LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
            try {
                Class.forName(lafInfo.getClassName());
            } catch(ClassNotFoundException ex) {
                continue;
            }
            String name = lafInfo.getName();
            model.addElement(name);
            if(lafInfo.getClassName().equals(lafId)) {
                model.setSelectedItem(name);
            }
        }
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String laf = (String) getSelectedItem();
                try {
                    boolean originallyDecorated = UIManager.getLookAndFeel().getSupportsWindowDecorations();
                    for(UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if(laf.equals(info.getName())) {
                            LOG.log(Level.INFO, "Setting L&F: {0}", info.getClassName());
                            try {
                                UIManager.setLookAndFeel(info.getClassName());
                            } catch(InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ex) {
                                Logger.getLogger(ThemeSelector.class.getName()).log(Level.SEVERE, null, ex);
                            } catch(ClassNotFoundException ex) {
                                //                                Logger.getLogger(ThemeSelector.class.getName()).log
                                // (Level.SEVERE, null, ex);
                                LOG.warning("Unable to load user L&F");
                            }
                        }
                    }
                    boolean decorate = UIManager.getLookAndFeel().getSupportsWindowDecorations();
                    boolean decorateChanged = decorate != originallyDecorated;
                    boolean frameDecorations = false; // TODO: Frame decoration
                    //                    JFrame.setDefaultLookAndFeelDecorated(decorate);
                    //                    JDialog.setDefaultLookAndFeelDecorated(decorate);
                    for(Window w : Window.getWindows()) {
                        SwingUtilities.updateComponentTreeUI(w);
                        if(decorateChanged && frameDecorations) {
                            w.dispose();
                            handle(w, decorate);//w.isVisible());
                            w.setVisible(true);
                        }
                    }
                } catch(Exception ex) {
                    LOG.log(Level.WARNING, "Failed loading L&F: " + laf, ex);
                }
            }

            private void handle(Window window, boolean decorations) {
                JRootPane rpc = null;
                if(window instanceof RootPaneContainer) {
                    rpc = ( (RootPaneContainer) window ).getRootPane();
                }
                if(window instanceof Frame) {
                    ( (Frame) window ).setUndecorated(decorations);
                } else if(window instanceof Dialog) {
                    ( (Dialog) window ).setUndecorated(decorations);
                } else {
                    LOG.log(Level.WARNING, "Unhandled setUndecorated mapping: {0}", window);
                }
                if(rpc != null) {
                    int decor = JRootPane.FRAME;
                    rpc.setWindowDecorationStyle(decor);
                }
            }
        });
    }
}
