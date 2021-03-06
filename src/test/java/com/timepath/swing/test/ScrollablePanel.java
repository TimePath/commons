package com.timepath.swing.test;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * A panel that implements the Scrollable interface. This class allows you to
 * customize the scrollable features by using newly provided setter methods so
 * you don't have to extend this class every time.
 * Scrollable amounts can be specifed as a percentage of the viewport size or as
 * an actual pixel value. The amount can be changed for both unit and block
 * scrolling for both horizontal and vertical scrollbars.
 * The Scrollable interface only provides a boolean value for determining
 * whether or not the viewport size (width or height) should be used by the
 * scrollpane when determining if scrollbars should be made visible. This class
 * supports the concept of dynamically changing this value based on the size of
 * the viewport. In this case the viewport size will only be used when it is
 * larger than the panels size. This has the effect of ensuring the viewport is
 * always full as components added to the panel will be size to fill the area
 * available, based on the rules of the applicable layout manager of course.
 */
public class ScrollablePanel extends JPanel implements Scrollable, SwingConstants {

    private ScrollableSizeHint scrollableHeight = ScrollableSizeHint.NONE;
    private ScrollableSizeHint scrollableWidth = ScrollableSizeHint.NONE;
    private IncrementInfo horizontalBlock;
    private IncrementInfo horizontalUnit;
    private IncrementInfo verticalBlock;
    private IncrementInfo verticalUnit;

    /**
     * Default constructor that uses a FlowLayout
     */
    public ScrollablePanel() {
        this(new FlowLayout());
    }

    /**
     * Constuctor for specifying the LayoutManager of the panel.
     *
     * @param layout the LayountManger for the panel
     */
    public ScrollablePanel(LayoutManager layout) {
        super(layout);
        @NotNull IncrementInfo block = new IncrementInfo(IncrementType.PERCENT, 100);
        @NotNull IncrementInfo unit = new IncrementInfo(IncrementType.PERCENT, 10);
        setScrollableBlockIncrement(HORIZONTAL, block);
        setScrollableBlockIncrement(VERTICAL, block);
        setScrollableUnitIncrement(HORIZONTAL, unit);
        setScrollableUnitIncrement(VERTICAL, unit);
    }

    public static void main(String... args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                @NotNull JFrame f = new JFrame();
                @NotNull ScrollablePanel sp = new ScrollablePanel();
                sp.add(new JFileChooser());
                f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                @NotNull JScrollPane scroller = new JScrollPane(sp,
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                f.setContentPane(scroller);
                f.pack();
                f.setVisible(true);
            }
        });
    }

    /**
     * Specify the information needed to do block scrolling.
     *
     * @param orientation specify the scrolling orientation. Must be either:
     *                    SwingContants.HORIZONTAL or SwingContants.VERTICAL.
     * @param info        An IncrementInfo object containing information of how to
     *                    calculate the scrollable amount.
     */
    public void setScrollableBlockIncrement(int orientation, IncrementInfo info) {
        switch (orientation) {
            case SwingConstants.HORIZONTAL:
                horizontalBlock = info;
                break;
            case SwingConstants.VERTICAL:
                verticalBlock = info;
                break;
            default:
                throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
    }

    /**
     * Specify the information needed to do unit scrolling.
     *
     * @param orientation specify the scrolling orientation. Must be either:
     *                    SwingContants.HORIZONTAL or SwingContants.VERTICAL.
     * @param info        An IncrementInfo object containing information of how to
     *                    calculate the scrollable amount.
     */
    public void setScrollableUnitIncrement(int orientation, IncrementInfo info) {
        switch (orientation) {
            case SwingConstants.HORIZONTAL:
                horizontalUnit = info;
                break;
            case SwingConstants.VERTICAL:
                verticalUnit = info;
                break;
            default:
                throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
    }

    /**
     * Get the height ScrollableSizeHint enum
     *
     * @return the ScrollableSizeHint enum for the height
     */
    public ScrollableSizeHint getScrollableHeight() {
        return scrollableHeight;
    }

    /**
     * Set the ScrollableSizeHint enum for the height. The enum is used to
     * determine the boolean value that is returned by the
     * getScrollableTracksViewportHeight() method. The valid values are:
     * ScrollableSizeHint.NONE - return "false", which causes the height of the
     * panel to be used when laying out the children ScrollableSizeHint.FIT -
     * return "true", which causes the height of the viewport to be used when
     * laying out the children ScrollableSizeHint.STRETCH - return "true" when
     * the viewport height is greater than the height of the panel, "false"
     * otherwise.
     *
     * @param scrollableHeight as represented by the ScrollableSizeHint enum.
     */
    public void setScrollableHeight(ScrollableSizeHint scrollableHeight) {
        this.scrollableHeight = scrollableHeight;
        revalidate();
    }

    /**
     * Get the width ScrollableSizeHint enum
     *
     * @return the ScrollableSizeHint enum for the width
     */
    public ScrollableSizeHint getScrollableWidth() {
        return scrollableWidth;
    }

    /**
     * Set the ScrollableSizeHint enum for the width. The enum is used to
     * determine the boolean value that is returned by the
     * getScrollableTracksViewportWidth() method. The valid values are:
     * ScrollableSizeHint.NONE - return "false", which causes the width of the
     * panel to be used when laying out the children ScrollableSizeHint.FIT -
     * return "true", which causes the width of the viewport to be used when
     * laying out the children ScrollableSizeHint.STRETCH - return "true" when
     * the viewport width is greater than the width of the panel, "false"
     * otherwise.
     *
     * @param scrollableWidth as represented by the ScrollableSizeHint enum.
     */
    public void setScrollableWidth(ScrollableSizeHint scrollableWidth) {
        this.scrollableWidth = scrollableWidth;
        revalidate();
    }

    /**
     * Get the block IncrementInfo for the specified orientation
     *
     * @return the block IncrementInfo for the specified orientation
     */
    public IncrementInfo getScrollableBlockIncrement(int orientation) {
        return orientation == SwingConstants.HORIZONTAL ? horizontalBlock : verticalBlock;
    }

    /**
     * Specify the information needed to do block scrolling.
     *
     * @param orientation specify the scrolling orientation. Must be either:
     *                    SwingContants.HORIZONTAL or SwingContants.VERTICAL.
     * @param amount      a value used with the IncrementType to determine the
     *                    scrollable amount
     * @paran type specify how the amount parameter in the calculation of the
     * scrollable amount. Valid values are: IncrementType.PERCENT - treat the
     * amount as a % of the viewport size IncrementType.PIXEL - treat the amount
     * as the scrollable amount
     */
    public void setScrollableBlockIncrement(int orientation, IncrementType type, int amount) {
        @NotNull IncrementInfo info = new IncrementInfo(type, amount);
        setScrollableBlockIncrement(orientation, info);
    }

    /**
     * Get the unit IncrementInfo for the specified orientation
     *
     * @return the unit IncrementInfo for the specified orientation
     */
    public IncrementInfo getScrollableUnitIncrement(int orientation) {
        return orientation == SwingConstants.HORIZONTAL ? horizontalUnit : verticalUnit;
    }

    /**
     * Specify the information needed to do unit scrolling.
     *
     * @param orientation specify the scrolling orientation. Must be either:
     *                    SwingContants.HORIZONTAL or SwingContants.VERTICAL.
     * @param amount      a value used with the IncrementType to determine the
     *                    scrollable amount
     * @paran type specify how the amount parameter in the calculation of the
     * scrollable amount. Valid values are: IncrementType.PERCENT - treat the
     * amount as a % of the viewport size IncrementType.PIXEL - treat the amount
     * as the scrollable amount
     */
    public void setScrollableUnitIncrement(int orientation, IncrementType type, int amount) {
        @NotNull IncrementInfo info = new IncrementInfo(type, amount);
        setScrollableUnitIncrement(orientation, info);
    }

    //  Implement Scrollable interface
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(@NotNull Rectangle visible, int orientation, int direction) {
        switch (orientation) {
            case SwingConstants.HORIZONTAL:
                return getScrollableIncrement(horizontalUnit, visible.width);
            case SwingConstants.VERTICAL:
                return getScrollableIncrement(verticalUnit, visible.height);
            default:
                throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
    }

    @Override
    public int getScrollableBlockIncrement(@NotNull Rectangle visible, int orientation, int direction) {
        switch (orientation) {
            case SwingConstants.HORIZONTAL:
                return getScrollableIncrement(horizontalBlock, visible.width);
            case SwingConstants.VERTICAL:
                return getScrollableIncrement(verticalBlock, visible.height);
            default:
                throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        if (scrollableWidth == ScrollableSizeHint.NONE) {
            return false;
        }
        if (scrollableWidth == ScrollableSizeHint.FIT) {
            return true;
        }
        //  STRETCH sizing, use the greater of the panel or viewport width
        if (getParent() instanceof JViewport) {
            return getParent().getWidth() > getPreferredSize().width;
        }
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (scrollableHeight == ScrollableSizeHint.NONE) {
            return false;
        }
        if (scrollableHeight == ScrollableSizeHint.FIT) {
            return true;
        }
        //  STRETCH sizing, use the greater of the panel or viewport height
        if (getParent() instanceof JViewport) {
            return getParent().getHeight() > getPreferredSize().height;
        }
        return false;
    }

    protected int getScrollableIncrement(@NotNull IncrementInfo info, int distance) {
        if (info.getIncrement() == IncrementType.PIXELS) {
            return info.getAmount();
        } else {
            return distance * info.getAmount() / 100;
        }
    }

    public enum ScrollableSizeHint {
        NONE,
        FIT,
        STRETCH
    }

    public enum IncrementType {
        PERCENT,
        PIXELS
    }

    /**
     * Helper class to hold the information required to calculate the scroll
     * amount.
     */
    public static class IncrementInfo {

        private IncrementType type;
        private int amount;

        public IncrementInfo(IncrementType type, int amount) {
            this.type = type;
            this.amount = amount;
        }

        public IncrementType getIncrement() {
            return type;
        }

        public int getAmount() {
            return amount;
        }

        @NotNull
        @Override
        public String toString() {
            return "ScrollablePanel[" + type + ", " + amount + "]";
        }
    }
}
