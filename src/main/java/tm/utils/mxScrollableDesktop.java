package tm.utils;

import javax.swing.*;
import java.awt.*;
import tm.ui.settings.TMTheme;


public class mxScrollableDesktop extends JDesktopPane {

	private Dimension cachedPreferredSize;

	@Override
    /**
     * Paints the scrollable desktop background.
     * @param g graphics context for painting
     **/
    protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(TMTheme.getThemeColors().get("WIN_BG"));
		g.fillRect(0, 0, getWidth(), getHeight());
	}

    /**
     * Creates a mxScrollableDesktop instance.
     **/
    public mxScrollableDesktop() {
        super();

        DesktopManager mgr = new DefaultDesktopManager() {
            /**
             * Revalidates after an internal frame drag ends.
             * @param f internal frame that finished dragging or resizing
             **/
            public void endDraggingFrame(JComponent f) {
                super.endDraggingFrame(f);
                invalidatePreferredSizeCache();
                revalidate();
            }

            /**
             * Revalidates after an internal frame resize ends.
             * @param f internal frame that finished dragging or resizing
             **/
            public void endResizingFrame(JComponent f) {
                super.endResizingFrame(f);
                invalidatePreferredSizeCache();
                revalidate();
            }
        };

		setDesktopManager(mgr);
		
		
    }

    /**
     * Clears cached preferred size so the next layout pass recomputes it.
     **/
    public void invalidatePreferredSizeCache() {
        cachedPreferredSize = null;
    }

    /**
     * Set the preferred size of the desktop to the right-bottom-corner of the
     * internal-frame with the "largest" right-bottom-corner.
     * @return The preferred desktop dimension.
     **/
    @Override
    public Dimension getPreferredSize() {
        if (cachedPreferredSize == null) {
            cachedPreferredSize = computePreferredSize();
        }
        return new Dimension(cachedPreferredSize);
    }

    private Dimension computePreferredSize() {
        JInternalFrame [] array = getAllFrames();
        int maxX = 0;
        int maxY = 0;
        for (int i = 0; i<array.length; i++) {
            int x = array[i].getX() + array[i].getWidth();
            if(x > maxX) {
                maxX = x;
            }
            int y = array[i].getY() + array[i].getHeight();
            if(y > maxY) {
                maxY = y;
            }
        }
        return new Dimension(maxX, maxY);
    }

}
