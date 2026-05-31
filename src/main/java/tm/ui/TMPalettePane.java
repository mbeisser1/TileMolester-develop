/*
*
*    Copyright (C) 2003 Kent Hansen.
*
*    This file is part of Tile Molester.
*
*    Tile Molester is free software; you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*
*    Tile Molester is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*/

/**
 * 2014-05-06
 * Added the shift palette functionality
 * Mewster
 **/
package tm.ui;

import tm.TMPalette;
import tm.colorcodecs.ColorCodec;
import tm.utils.DecimalNumberVerifier;
import tm.utils.TMLog;

import javax.swing.*;
import javax.swing.event.*;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The palette pane contains the following components:
 * - Foreground color box
 * - Background color box
 * - Palette vizualiser (see TMPaletteVizualiser)
 * - FG/BG swap button
 * - Arrow up/down for switching palette index
 **/
public class TMPalettePane extends JPanel implements MouseInputListener {

    private TMView view;
    private final TMUI ui;
    private final TMPaletteVizualiser vizualiser;
    private final ColorBox fgColorBox = new ColorBox();
    private final ColorBox bgColorBox = new ColorBox();
    private final Cursor pickupCursor;
    private JButton decButton = new JButton(new FlatSVGIcon("icons/fluent/caret_left_24_filled.svg", 32, 32));
    private JButton incButton = new JButton(new FlatSVGIcon("icons/fluent/caret_right_24_filled.svg", 32, 32));
    private JButton rotatePaletteRightButton = new JButton(new FlatSVGIcon("icons/fluent/rotate_right_24_regular.svg", 32, 32));
    private JButton rotatePaletteLeftButton = new JButton(new FlatSVGIcon("icons/fluent/rotate_left_24_regular.svg", 32, 32));
    private JButton leftShiftButton = new JButton(TMUIConstants.toolbarIcon("icons/fluent/caret_left_24_filled.svg"));
    private JButton rightShiftButton = new JButton(TMUIConstants.toolbarIcon("icons/fluent/caret_right_24_filled.svg"));
    private JButton swapButton = new JButton(TMUIConstants.toolbarIcon("icons/fluent/custom/swap.svg"));
	
	private JTextField shiftValueField = new JTextField("1");

    /**
     * Baseline native palette entries per palIndex, captured on first rotate.
     **/
    private Map<Integer, int[]> palettePageBaseline = new HashMap<>();

    /**
     * Creates a palette pane.
     **/
    public TMPalettePane(TMUI ui) {
        this.ui = ui;
        ClassLoader cl = getClass().getClassLoader();
        pickupCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                new ImageIcon(cl.getResource("icons/DropperCursor24.gif")).getImage(),
                new Point(8, 23), "Dropper");
        vizualiser = new TMPaletteVizualiser();
        //setBorder(new EtchedBorder(EtchedBorder.RAISED));
        swapButton.setBorder(null);
        // set up UI
        setLayout(null);    // no layout manager, want to place and size components pixel-perfect

        shiftValueField.addKeyListener(new DecimalNumberVerifier());

        // add components
        add(fgColorBox);
        add(bgColorBox);
        add(swapButton);
        add(vizualiser);
        add(decButton);
        add(incButton);
        add(rotatePaletteRightButton);
        add(rotatePaletteLeftButton);
        add(leftShiftButton);
        add(rightShiftButton);
        add(shiftValueField);

        // set sizes
        fgColorBox.setSize(32, 32);
        bgColorBox.setSize(32, 32);
        swapButton.setSize(32, 32);
        vizualiser.setSize(256, 64);
        decButton.setSize(32, 64);
        incButton.setSize(32, 64);
        rotatePaletteRightButton.setSize(32, 64);
        rotatePaletteLeftButton.setSize(32, 64);
        leftShiftButton.setSize(32, 32);
        rightShiftButton.setSize(32, 32);
        shiftValueField.setSize(64, 20);

        // set positions
        fgColorBox.setLocation(8, 8);
        bgColorBox.setLocation(40, 40);
        swapButton.setLocation(8, 40);
        decButton.setLocation(80, 8);
        vizualiser.setLocation(112, 8);
        incButton.setLocation(368, 8);
        rotatePaletteRightButton.setLocation(400, 8);
        rotatePaletteLeftButton.setLocation(432, 8);
        leftShiftButton.setLocation(464, 24);
        shiftValueField.setLocation(504, 30);
        rightShiftButton.setLocation(574, 24);

        vizualiser.setCursor(pickupCursor);
        vizualiser.addMouseListener(this);

        swapButton.setFocusable(false);
        swapButton.addActionListener(
            new ActionListener() {
                /**
                 * Invokes {@link #swapColors()} in response to the user action.
                 * @param e event from the AWT/Swing listener
                 **/
                public void actionPerformed(ActionEvent e) {
                    swapColors();
                }
            }
        );
        decButton.setFocusable(false);
        decButton.addActionListener(
            new ActionListener() {
                /**
                 * Invokes {@link #setPreviousPalIndex()} in response to the user action.
                 * @param e event from the AWT/Swing listener
                 **/
                public void actionPerformed(ActionEvent e) {
                    setPreviousPalIndex();
                }
            }
        );
        incButton.setFocusable(false);
        incButton.addActionListener(
            new ActionListener() {
                /**
                 * Invokes {@link #setNextPalIndex()} in response to the user action.
                 * @param e event from the AWT/Swing listener
                 **/
                public void actionPerformed(ActionEvent e) {
                    setNextPalIndex();
                }
            }
        );

        rotatePaletteRightButton.setToolTipText(ui.xlate("Palette_Rotate_Right"));
        rotatePaletteRightButton.setFocusable(false);
        rotatePaletteRightButton.addActionListener(
            new ActionListener() {
                /**
                 * Invokes {@link #shiftCurrentPalettePageRight()} in response to the user action.
                 * @param e event from the AWT/Swing listener
                 **/
                public void actionPerformed(ActionEvent e) {
                    shiftCurrentPalettePageRight();
                }
            }
        );

        rotatePaletteLeftButton.setToolTipText(ui.xlate("Palette_Rotate_Left"));
        rotatePaletteLeftButton.setFocusable(false);
        rotatePaletteLeftButton.addActionListener(
            new ActionListener() {
                /**
                 * Invokes {@link #shiftCurrentPalettePageLeft()} in response to the user action.
                 * @param e event from the AWT/Swing listener
                 **/
                public void actionPerformed(ActionEvent e) {
                    shiftCurrentPalettePageLeft();
                }
            }
        );

        leftShiftButton.setFocusable(false);
        leftShiftButton.addActionListener(
                new ActionListener() {
                    /**
                     * Invokes {@link #getShift()} in response to the user action.
                     * @param e event from the AWT/Swing listener
                     **/
                    public void actionPerformed(ActionEvent e) {
                        shiftPalette(-getShift());
                    }
                }
        );

        rightShiftButton.setFocusable(false);
        rightShiftButton.addActionListener(
                new ActionListener() {
                    /**
                     * Invokes {@link #getShift()} in response to the user action.
                     * @param e event from the AWT/Swing listener
                     **/
                    public void actionPerformed(ActionEvent e) {
                        shiftPalette(+getShift());
                    }
                }
        );

       lockShiftButtons();

       setPreferredSize(new Dimension(2048, 80));
    }

    /**
     * Sets the palette to be rendered.
     * @param palette palette whose colors are displayed or edited
     **/
    public void setPalette(TMPalette palette) {
        if (palette == null) {
            lockShiftButtons();
            vizualiser.setPalette(null);
            return;
        }
        if (palette.isDirect()) {
            lockShiftButtons();
        } else {
            unlockShiftButtons();
        }
        vizualiser.setPalette(palette);
    }

    /**
     * Sets the palette index from which to start displaying colors.
     * @param palIndex palette page index
     **/
    public void setPalIndex(int palIndex) {
        vizualiser.setPalIndex(palIndex);
    }

    /**
     * Sets the bitdepth, i.e. how many colors to display.
     * @param bitDepth bits per pixel determining how many colors are shown
     **/
    public void setBitDepth(int bitDepth) {
        vizualiser.setBitDepth(bitDepth);
    }

    /**
     * Sets the foreground color.
     * @param fgColor foreground draw color as 32-bit ARGB
     **/
    public void setFGColor(int fgColor) {
        fgColorBox.setColor(fgColor);
    }

    /**
     * Sets the background color.
     * @param bgColor background color used to clear pixels
     **/
    public void setBGColor(int bgColor) {
        bgColorBox.setColor(bgColor);
    }

    /**
     * Called when a view has been selected.
     * Loads and displays the view's palette according to current settings.
     * @param view file view associated with this component
     **/
    public void viewSelected(TMView view) {
        if (view == null) {
            this.view = null;
            vizualiser.setPalette(null);
            repaint();
            return;
        }
        if (this.view != view) {
            palettePageBaseline.clear();
        }
        this.view = view;
        TMPalette palette = view.getPalette();
        int palIndex = view.getPalIndex();
        int bitDepth = view.getTileCodec().getBitsPerPixel();
        vizualiser.configure(palette, palIndex, bitDepth);
        if (palette != null) {
            if (palette.isDirect()) {
                lockShiftButtons();
            } else {
                unlockShiftButtons();
            }
        } else {
            lockShiftButtons();
        }
        setFGColor(view.getFGColor());
        setBGColor(view.getBGColor());
        boolean palettized = view.getTileCodec().getBitsPerPixel() <= 8;
        rotatePaletteRightButton.setEnabled(palettized);
        rotatePaletteLeftButton.setEnabled(palettized);
        repaint();
    }

    /**
     * Called when user clicked on a color.
     * Set the color as foreground or background color depending on which button was pressed.
     * @param e event from the AWT/Swing listener
     **/
    public void mousePressed(MouseEvent e) {
        if (view == null) {
            return;
        }
        // get the color
        int color = vizualiser.getColorAt(e.getX(), e.getY());
        // set it
        if (e.getButton() == MouseEvent.BUTTON1) {
            // set as foreground color
            ui.setFGColor(color);
        }
        else {
            // set as background color
            ui.setBGColor(color);
        }
    }

// Other mouse events, not used yet...

    /**
     * Opens the color chooser on double-click to edit a palette entry.
     * @param e event from the AWT/Swing listener
     **/
    public void mouseClicked(MouseEvent e) {
        if (view == null || e.getClickCount() != 2) {
            return;
        }
        // let user edit the color
        Color newColor = JColorChooser.showDialog(ui, "Edit Color", new Color(fgColorBox.getColor()));
        if (newColor != null) {
            boolean equal = view.getFGColor() == view.getBGColor();
            int rgb = newColor.getRGB();
            int colorIndex = vizualiser.getIndexOfColorAt(e.getX(), e.getY());
            TMPalette palette = view.getPalette();

            /* view.addReversibleAction(
                new ReversiblePaletteEditAction(
                    view,
                    palette,
                    colorIndex,
                    palette.getEntryRGB(colorIndex),
                    rgb
                )
            ;*/

            // set the new color(s)
            palette.setEntryRGB(colorIndex, rgb);
            ui.setFGColor(palette.getEntryRGB(colorIndex));
            if (equal) {
                ui.setBGColor(palette.getEntryRGB(colorIndex));
            }

            // PS: If palette is NOT direct then this means fileimage.modified!!
            if (!palette.isDirect()) {
                byte[] src = palette.getEntryBytes(colorIndex);
                byte[] dest = view.getFileImage().getContents();
                System.arraycopy(src, 0, dest, palette.getOffset()+(colorIndex*src.length), src.length);
                ui.fileImageModified(view.getFileImage());
            }

            // redraw stuff
            view.refreshPaletteDisplay();
            repaint();
        }
    }

    /**
     * @param e event from the AWT/Swing listener
     **/
    public void mouseDragged(MouseEvent e) { }
    /**
     * @param e event from the AWT/Swing listener
     **/
    public void mouseMoved(MouseEvent e) { }
    /**
     * @param e event from the AWT/Swing listener
     **/
    public void mouseEntered(MouseEvent e) { }
    /**
     * @param e event from the AWT/Swing listener
     **/
    public void mouseExited(MouseEvent e) { }
    /**
     * @param e event from the AWT/Swing listener
     **/
    public void mouseReleased(MouseEvent e) { }

    /**
     * A "color box" is merely a label that is painted with a color.
     **/
    private class ColorBox extends JLabel {

        private int color;

        /**
         * Sets the color shown in the color box and repaints it.
         * @param color 32-bit ARGB color value
         **/
        public void setColor(int color) {
            this.color = color;
            repaint();
        }

        /**
         * Gets the color currently shown in the color box.
         * @return 32-bit ARGB color stored in the color box
         **/
        public int getColor() {
            return color;
        }

        /**
         * @param g graphics context used for drawing
         **/
        public void paintComponent(Graphics g) {
            g.setColor(new Color(color));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

    }

    /**
     * Switches to the previous palette index.
     * Wrap-around is employed if the current index is the first one (0).
     **/
    public void setPreviousPalIndex() {
        if (view == null) {
            return;
        }
        view.setKeysEnabled(false);
        //
        resetPalettePageRotation(view.getPalIndex());
        int pi = view.getPalIndex();
        pi = (pi == 0) ? view.getPalIndexMaximum() : pi-1;
        view.mapDrawColorsToPalIndex(pi);
        view.setPalIndex(pi);
        viewSelected(view);
        //
        view.setKeysEnabled(true);
    }

    /**
     * Switches to the next palette index.
     * Wrap-around is employed if the current index is the last one (maximum).
     **/
    public void setNextPalIndex() {
        if (view == null) {
            return;
        }
        view.setKeysEnabled(false);
        //
        resetPalettePageRotation(view.getPalIndex());
        int pi = view.getPalIndex();
        pi = (pi == view.getPalIndexMaximum()) ? 0 : pi+1;
        view.mapDrawColorsToPalIndex(pi);
        view.setPalIndex(pi);
        viewSelected(view);
        //
        view.setKeysEnabled(true);
    }

    /**
     * Swaps the foreground and background colors.
     **/
    public void swapColors() {
        int fg = fgColorBox.getColor();
        ui.setFGColor(bgColorBox.getColor());
        ui.setBGColor(fg);
    }

    /**
     * Gets the number inside the shiftValueField
     * @return the shift to perform
     * Gets the shift.
     * @return palette shift amount entered by the user
     **/
    public int getShift() {
        try {
            return Integer.parseInt(shiftValueField.getText());
        } catch (NumberFormatException e) {
            TMLog.warning("Invalid palette shift value; using 1", e);
            return 1;
        }
    }

	/**
	 * Gets the current vizualiser
	 * Gets the vizualiser.
	 * @return palette visualization panel
	 **/
	public TMPaletteVizualiser getVizualiser() {
		return vizualiser;
	}

    /**
     * @param shift number of palette entries to shift by
     **/
    public void shiftPalette(int shift) {
        if (view == null) {
            return;
        }
        view.setKeysEnabled(false);
        //
        TMPalette palette = view.getPalette();
        int offset = palette.getOffset();
        int size = palette.getSize();
        ColorCodec codec = palette.getCodec();
        int endianness = palette.getEndianness();
        boolean copy = palette.isDirect();
        boolean modified = palette.isModified();

        // create the palette
        byte[] data = view.getFileImage().getContents();

        //checks palette bounds
        int newOffset = offset+shift;
        //System.out.println("Filesize="+view.getFileImage().getSize()+" pixelsize="+codec.getBytesPerPixel()+" newoffset="+newOffset+" palettesize="+size); USEFUL IN CASE I MESSED THE MATH
        if (newOffset < 0){
            newOffset = 0;
        }
        else if (newOffset+(codec.getBytesPerPixel()*size)>=view.getFileImage().getSize()) {
                newOffset = view.getFileImage().getSize()-(codec.getBytesPerPixel()*size);
        }

        palette = new TMPalette("ID", data, newOffset, size, codec, endianness, copy, modified);

        // set the new palette
        view.setPalette(palette);
        viewSelected(view);
        ui.refreshStatusBar();
        //
        view.setKeysEnabled(true);
    }

    /**
     * Locks the palette shifting buttons
     **/
    public void lockShiftButtons() {
        leftShiftButton.setEnabled(false);
        rightShiftButton.setEnabled(false);
    }

    /**
     * Unlocks the palette shifting buttons
     **/
    public void unlockShiftButtons() {
        leftShiftButton.setEnabled(true);
        rightShiftButton.setEnabled(true);
    }

    /**
     * @param palIndex palette page index
     * @param palette palette whose colors are displayed or edited
     * @param base base value
     * @param count count value
     **/
    private void ensureBaseline(int palIndex, TMPalette palette, int base, int count) {
        if (!palettePageBaseline.containsKey(palIndex)) {
            int[] baseline = new int[count];
            for (int i = 0; i < count; i++) {
                baseline[i] = palette.getEntry(base + i);
            }
            palettePageBaseline.put(palIndex, baseline);
        }
    }

    /**
     * @param palette palette whose colors are displayed or edited
     * @param base base value
     * @param count count value
     **/
    private void syncPalettePageToFile(TMPalette palette, int base, int count) {
        if (palette.isDirect()) {
            return;
        }
        byte[] dest = view.getFileImage().getContents();
        int bytesPerPixel = palette.getCodec().getBytesPerPixel();
        for (int i = 0; i < count; i++) {
            int idx = base + i;
            byte[] src = palette.getEntryBytes(idx);
            System.arraycopy(src, 0, dest, palette.getOffset() + idx * bytesPerPixel, src.length);
        }
        ui.fileImageModified(view.getFileImage());
    }

    /**
     *
     **/
    private void updateDrawColorsForCurrentPage() {
        if (view.getTileCodec().getBitsPerPixel() > 8) {
            return;
        }
        TMPalette pal = view.getPalette();
        int palIndex = view.getPalIndex();
        int colorCount = view.getTileCodec().getColorCount();
        int colorIndex = palIndex * colorCount;
        int fgIndex = pal.indexOf(colorIndex, view.getFGColor());
        int bgIndex = pal.indexOf(colorIndex, view.getBGColor());
        if (fgIndex >= 0) {
            ui.setFGColor(pal.getEntryRGB(colorIndex + fgIndex));
        }
        if (bgIndex >= 0) {
            ui.setBGColor(pal.getEntryRGB(colorIndex + bgIndex));
        }
    }

    /**
     * @param palette palette whose colors are displayed or edited
     * @param base base value
     * @param entries entries value
     **/
    private void applyPageEntries(TMPalette palette, int base, int[] entries) {
        for (int i = 0; i < entries.length; i++) {
            palette.setEntry(base + i, entries[i]);
        }
        syncPalettePageToFile(palette, base, entries.length);
        updateDrawColorsForCurrentPage();
        view.refreshPaletteDisplay();
        viewSelected(view);
    }

    /**
     * @param palIndex palette page index
     **/
    public void resetPalettePageRotation(int palIndex) {
        if (view == null || view.getTileCodec().getBitsPerPixel() > 8) {
            return;
        }
        int[] baseline = palettePageBaseline.get(palIndex);
        if (baseline == null) {
            return;
        }
        TMPalette palette = view.getPalette();
        int base = palIndex * view.getTileCodec().getColorCount();
        applyPageEntries(palette, base, baseline);
    }

    /**
     *
     **/
    public void shiftCurrentPalettePageRight() {
        shiftCurrentPalettePage(+1);
    }

    /**
     *
     **/
    public void shiftCurrentPalettePageLeft() {
        shiftCurrentPalettePage(-1);
    }

    /**
     * @param direction rotation direction (+1 right, -1 left)
     **/
    private void shiftCurrentPalettePage(int direction) {
        if (view == null || view.getTileCodec().getBitsPerPixel() > 8) {
            return;
        }
        view.setKeysEnabled(false);
        TMPalette palette = view.getPalette();
        int palIndex = view.getPalIndex();
        int colorCount = view.getTileCodec().getColorCount();
        int base = palIndex * colorCount;

        ensureBaseline(palIndex, palette, base, colorCount);

        int[] shifted = new int[colorCount];
        for (int i = 0; i < colorCount; i++) {
            shifted[i] = palette.getEntry(base + i);
        }
        if (direction > 0) {
            // last -> first, rest shift right
            int last = shifted[colorCount - 1];
            for (int i = colorCount - 1; i >= 1; i--) {
                shifted[i] = shifted[i - 1];
            }
            shifted[0] = last;
        } else {
            // first -> last, rest shift left
            int first = shifted[0];
            for (int i = 0; i < colorCount - 1; i++) {
                shifted[i] = shifted[i + 1];
            }
            shifted[colorCount - 1] = first;
        }

        for (int i = 0; i < colorCount; i++) {
            palette.setEntry(base + i, shifted[i]);
        }
        syncPalettePageToFile(palette, base, colorCount);
        updateDrawColorsForCurrentPage();
        view.refreshPaletteDisplay();
        viewSelected(view);
        view.setKeysEnabled(true);
    }

}