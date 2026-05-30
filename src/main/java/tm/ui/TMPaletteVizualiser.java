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

package tm.ui;

import tm.TMPalette;
import javax.swing.*;
import java.awt.*;

/**
 * Panel where a TMPalette is visualized.
 **/
public class TMPaletteVizualiser extends JPanel {

    private TMPalette palette=null;
    private int palIndex;
    private int bitDepth;
    private int colorCount;
	private int lastIndex = 0;

    /**
     * Sets the palette that is to be displayed.
     * @param palette palette whose colors are displayed or edited
     **/
    public void setPalette(TMPalette palette) {
        this.palette = palette;
    }

    /**
     * Sets the palette index.
     * @param palIndex palette page index
     **/
    public void setPalIndex(int palIndex) {
        this.palIndex = palIndex;
        // keep lastIndex in same page when palette page changes
        // to avoid drift/messed-up mapping after cycling
        this.lastIndex = this.palIndex * this.colorCount;
    }

    /**
     * Gets the last palette index.
     * @return last palette index chosen by the user
     **/
    public int getLastIndex() {
        return this.lastIndex;
    }

    /**
     * Sets the bit depth.
     * @param bitDepth bits per pixel determining how many colors are shown
     **/
    public void setBitDepth(int bitDepth) {
        if (bitDepth > 8)
            bitDepth = 8;
        this.bitDepth = bitDepth;
        colorCount = 1 << bitDepth;
    }

    /**
     * Paints the palette.
     * @param g graphics context used for drawing
     **/
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (palette != null) {
            int colorIndex = palIndex * colorCount;
            int w = getBoxWidth();
            int h = getBoxHeight();
            // draw color boxes
            int x=0, y=0;
            for (int i=0; i<colorCount; i++) {
                // figure out location of color box
                switch (bitDepth) {
                    case 1:
                    case 2:
                    case 3:
                    x = i * w;
                    y = 0;
                    break;
                    case 4:
                    x = (i % 8) * w;
                    y = (i / 8) * h;
                    break;
                    case 5:
                    case 6:
                    x = (i % 16) * w;
                    y = (i / 16) * h;
                    break;
                    case 7:
                    case 8:
                    x = (i % 32) * w;
                    y = (i / 32) * h;
                    break;
                }
                // draw it
                g.setColor(new Color(palette.getEntryRGB(colorIndex+i)));
                g.fillRect(x, y, w, h);
            }
        }
    }

    /**
     * Gets the index of the color whose rectangle contains the point (x,y).
     * @return absolute palette index for the color at (x, y)
     * @param x horizontal pixel or tile coordinate
     * @param y vertical pixel or tile coordinate
     **/
    public int getIndexOfColorAt(int x, int y) {
        int colorIndex = palIndex * colorCount;
        int w = getBoxWidth();
        int h = getBoxHeight();
        int colorsPerRow = getWidth() / w;
        x /= w;
        y /= h;
        int i = (y * colorsPerRow) + x;
		lastIndex = colorIndex + i;
        return lastIndex;
    }

    /**
     * Gets the RGB value of the color whose rectangle contains the point (x,y).
     * @return 32-bit RGB value for the color at (x, y)
     * @param x horizontal pixel or tile coordinate
     * @param y vertical pixel or tile coordinate
     **/
    public int getColorAt(int x, int y) {
        int i = getIndexOfColorAt(x, y);
        return palette.getEntryRGB(i);
    }

    /**
     * Gets the height of a color rectangle according to bitdepth.
     * @return width of one palette color swatch
     **/
    public int getBoxWidth() {
        switch (bitDepth) {
            case 1:
            case 2:
            case 3:
            return getWidth() / colorCount;
            case 4:
            return getWidth() / 8;
            case 5:
            case 6:
            return getWidth() / 16;
            case 7:
            case 8:
            return getWidth() / 32;
        }
        return getWidth();
    }

    /**
     * Gets the width of a color rectangle according to bitdepth.
     * @return height of one palette color swatch
     **/
    public int getBoxHeight() {
        switch (bitDepth) {
            case 1:
            case 2:
            case 3:
            return getHeight();
            case 4:
            case 5:
            return getHeight() / 2;
            case 6:
            case 7:
            return getHeight() / 4;
            case 8:
            return getHeight() / 8;
        }
        return getHeight();
    }

}