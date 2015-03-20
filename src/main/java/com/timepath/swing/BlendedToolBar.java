package com.timepath.swing;

import com.timepath.plaf.OS;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
@SuppressWarnings("serial")
public class BlendedToolBar extends JToolBar implements MouseListener, MouseMotionListener {

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    private static final Logger LOG = Logger.getLogger(BlendedToolBar.class.getName());
    @Nullable
    private JFrame window;
    private Point wloc, mloc;

    /**
     * Creates new form BlendedToolBar
     */
    public BlendedToolBar() {
        initComponents();
        @NotNull JMenuBar menu = new JMenuBar();
        add(menu);
        menu.setVisible(false);
    }
    //    @Override
    //    protected void paintComponent(Graphics g) {
    ////        if(Main.os == OS.Mac) { // Has its own metal look
    ////            super.paintComponent(g);
    ////            return;
    ////        }
    //        this.setForeground(menu.getForeground());
    //        this.setBackground(menu.getBackground());
    //
    //        g.setColor(this.getBackground());
    //        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    //    }

    @NotNull
    @Override
    public Component add(@NotNull Component comp) {
        //        comp.setForeground(new Color(menu.getForeground().getRGB()));
        //        comp.setBackground(new Color(menu.getBackground().getRGB()));
        return super.add(comp);
    }

    private void initComponents() {
        setFloatable(false);
        setMinimumSize(new Dimension(24, 24));
        setPreferredSize(new Dimension(24, 24));
    }

    public void setWindow(@Nullable JFrame window) {
        this.window = window;
        if ((window != null) && !OS.Companion.isWindows()) {
            addMouseListener(this);
            addMouseMotionListener(this);
        }
    }

    @Override
    public void mouseDragged(@NotNull MouseEvent e) {
        @NotNull Point p = e.getLocationOnScreen();
        window.setLocation((wloc.x + p.x) - mloc.x, (wloc.y + p.y) - mloc.y);
        setCursor(new Cursor(Cursor.MOVE_CURSOR));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(@NotNull MouseEvent e) {
        setCursor(new Cursor(Cursor.MOVE_CURSOR));
        wloc = window.getLocation();
        mloc = e.getLocationOnScreen();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
