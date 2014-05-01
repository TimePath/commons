package com.timepath.swing.test;

import java.awt.*;
import javax.swing.*;

public class ScrollExample extends JPanel {

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        g.setColor(Color.green);
        g.fillOval(0, 0, 400, 400);

    }

    public static void main(String[] args) {

        JFrame f = new JFrame("Scroll Example");
        ScrollExample p = new ScrollExample();
        p.setPreferredSize(new Dimension(1000, 1000));

        JScrollPane scroller = new JScrollPane(p, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                               JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroller.getHorizontalScrollBar().setUnitIncrement(10);
        scroller.getVerticalScrollBar().setUnitIncrement(10);

        f.setPreferredSize(new Dimension(500, 500));
        f.add(scroller, BorderLayout.CENTER);
        f.pack();
        f.show();
    }

}